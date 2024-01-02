package nus.iss.trainify.controller;

import nus.iss.trainify.model.Video;
import nus.iss.trainify.model.Workout;
import nus.iss.trainify.service.YoutubeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class WorkoutController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Qualifier("redis") // Specify the qualifier for the desired RedisTemplate bean
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private YoutubeService youtubeService;

    private static final String REDIS_KEY_PREFIX = "workout:";


    @PostMapping("/add")
    public String addWorkout(@RequestParam("username") String username, @RequestBody Workout workout) {
        String key = REDIS_KEY_PREFIX + username;

        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        String workoutString = serializeWorkout(workout);

        if (listOperations != null) {
            // Add the serialized workout string to the Redis list
            listOperations.rightPush(key, workoutString);
            return "Workout added successfully";
        } else {
            return "Failed to add workout";
        }
    }

    @GetMapping("/get/{username}")
    public List<Workout> getWorkouts(@PathVariable String username) {
        String key = REDIS_KEY_PREFIX + username;

        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        if (listOperations != null) {
            // Retrieve the list of workout maps from Redis
            List<String> workoutStrings = listOperations.range(key, 0, -1);

            // Convert the list of strings to a list of Workout objects
            return workoutStrings.stream()
                    .map(this::deserializeWorkoutString)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    // Convert Workout to Map
    private String serializeWorkout(Workout workout) {
        try {
            return objectMapper.writeValueAsString(workout);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Workout object", e);
        }
    }

    // Deserialize Workout string to Workout object
    private Workout deserializeWorkoutString(String workoutString) {
        try {
            return objectMapper.readValue(workoutString, Workout.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize Workout string", e);
        }
    }

    @GetMapping("/dashboard")
    public String dashboardPage(@Valid @ModelAttribute("workout") Workout workout, @RequestParam("username") String username, Model model) {

        model.addAttribute("workout", new Workout());
        String key = REDIS_KEY_PREFIX + username;

        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        if (listOperations != null) {
            // Retrieve the list of workout strings from Redis
            List<String> workoutStrings = listOperations.range(key, 0, -1);

            // Convert the list of strings to a list of Workout objects
            List<Workout> userWorkouts = workoutStrings.stream()
                    .map(this::deserializeWorkoutString)
                    .collect(Collectors.toList());
            
            List<Workout> reversedUserWorkouts = new ArrayList<>(userWorkouts);
            Collections.reverse(reversedUserWorkouts);   

            // Calculate streaks based on userWorkouts
            int streak = calculateStreak(userWorkouts);

            model.addAttribute("username", username);
            model.addAttribute("streak", streak);
            model.addAttribute("userWorkouts", reversedUserWorkouts);
            return "dashboard";
        } else {
            return null;
        }
    }
     
    // Streak calculator, need to create logic such that streak will be gone after certain conditions
    private int calculateStreak(List<Workout> workouts) {
        int streak = 0;
        for (Workout workout : workouts) {
            if (workout.isCompleted()) {
                streak++;
            } else {
                break; // Streak is broken if a workout is not completed
            }
        }
        return streak;
    }

    @PostMapping("/workout/log-ippt")
    public String logIPPT(@Valid @ModelAttribute("workout") Workout workout,
                          BindingResult result,
                          Model model,
                          @RequestParam("username") String username
                          ) {

        if (result.hasErrors()) {
            return "dashboard";
        }

        int ipptScore = calculateIPPTScore(workout.getPushUpCount(),workout.getSitUpCount(),workout.getRunTime(),workout.getAge());

        // Create a new Workout object for the IPPT test
        Workout ipptWorkout = new Workout(username, "IPPT Test", true);
        ipptWorkout.setIpptScore(ipptScore);
        ipptWorkout.setPushUpCount(workout.getPushUpCount());
        ipptWorkout.setSitUpCount(workout.getSitUpCount());
        ipptWorkout.setRunTime(workout.getRunTime());

        // Save the IPPT workout record
        addWorkout(username, ipptWorkout);

        return "redirect:/dashboard?username="+username;
    }

    // IPPT score calculator
    private int calculateIPPTScore(int pushUpCount, int sitUpCount, double runTime, int age) {
        
        int[][] pushAndSitScoringTable = {
                { 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 },
                { 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 },
                { 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 },
                { 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 },
                { 23, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 },
                { 23, 23, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25 },
                { 23, 23, 24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25 },
                { 23, 23, 23, 23, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25 },
                { 22, 23, 23, 23, 24, 24, 24, 24, 25, 25, 25, 25, 25, 25 },
                { 22, 22, 23, 23, 23, 23, 24, 24, 25, 25, 25, 25, 25, 25 },
                { 22, 22, 23, 23, 23, 23, 24, 24, 24, 25, 25, 25, 25, 25 },
                { 22, 22, 22, 22, 23, 23, 23, 23, 24, 25, 25, 25, 25, 25 },
                { 21, 22, 22, 22, 23, 23, 23, 23, 24, 24, 25, 25, 25, 25 },
                { 21, 21, 22, 22, 22, 22, 23, 23, 23, 24, 24, 25, 25, 25 },
                { 21, 21, 22, 22, 22, 22, 23, 23, 23, 24, 24, 25, 25, 25 },
                { 21, 21, 21, 21, 22, 22, 22, 22, 23, 23, 24, 24, 25, 25 },
                { 21, 21, 21, 21, 22, 22, 22, 22, 23, 23, 23, 24, 25, 25 },
                { 20, 20, 21, 21, 21, 21, 22, 22, 22, 23, 23, 24, 24, 25 },
                { 20, 20, 21, 21, 21, 21, 22, 22, 22, 22, 23, 23, 24, 25 },
                { 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 23, 24, 24 },
                { 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 23, 23, 24 },
                { 19, 20, 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 23, 24 },
                { 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 21, 22, 23, 23 },
                { 18, 19, 19, 20, 20, 20, 20, 20, 21, 21, 21, 22, 22, 23 },
                { 18, 18, 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 22, 23 },
                { 17, 18, 18, 19, 19, 20, 20, 20, 20, 20, 21, 21, 22, 22 },
                { 16, 17, 18, 18, 19, 19, 20, 20, 20, 20, 20, 21, 21, 22 },
                { 15, 16, 17, 18, 18, 19, 19, 20, 20, 20, 20, 21, 21, 22 },
                { 14, 15, 16, 17, 18, 18, 19, 19, 20, 20, 20, 20, 21, 21 },
                { 14, 14, 15, 16, 17, 18, 18, 19, 19, 20, 20, 20, 20, 21 },
                { 13, 14, 14, 15, 16, 17, 18, 18, 19, 19, 20, 20, 20, 21 },
                { 13, 13, 14, 14, 16, 16, 17, 18, 18, 19, 19, 20, 20, 20 },
                { 12, 13, 13, 14, 15, 16, 16, 17, 18, 18, 19, 19, 20, 20 },
                { 11, 12, 13, 13, 14, 15, 16, 16, 17, 18, 18, 19, 19, 20 },
                { 10, 11, 12, 13, 14, 14, 15, 16, 16, 17, 18, 18, 19, 19 },
                { 9, 10, 11, 12, 13, 14, 14, 15, 16, 16, 17, 18, 18, 19 },
                { 8, 9, 10, 11, 12, 13, 14, 14, 15, 16, 16, 17, 18, 18 },
                { 7, 8, 9, 10, 11, 12, 13, 14, 14, 15, 16, 16, 17, 18 },
                { 7, 7, 8, 9, 10, 11, 12, 13, 14, 14, 15, 16, 16, 17 },
                { 6, 7, 7, 8, 9, 10, 11, 12, 13, 14, 14, 15, 16, 16 },
                { 6, 6, 7, 7, 8, 9, 10, 11, 12, 13, 14, 14, 15, 16 },
                { 5, 6, 6, 7, 7, 8, 9, 10, 11, 12, 13, 14, 14, 15 },
                { 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12, 13, 14, 14 },
                { 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12, 13, 14 },
                { 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12, 13 },
                { 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11, 12 },
                { 0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10, 11 },
                { 0, 0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9, 10 },
                { 0, 0, 0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8, 9 },
                { 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 6, 7, 7, 8 },
                { 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 6, 7, 7 },
                { 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 6, 7 },
                { 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 6 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
            };
        
        int[][] RunScoringTable = {
                { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 },
                { 49, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 },
                { 48, 49, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 },
                { 46, 48, 49, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 },
                { 44, 46, 48, 49, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 },
                { 43, 44, 46, 48, 49, 50, 50, 50, 50, 50, 50, 50, 50, 50 },
                { 42, 43, 44, 46, 48, 49, 50, 50, 50, 50, 50, 50, 50, 50 },
                { 41, 42, 43, 45, 46, 48, 49, 50, 50, 50, 50, 50, 50, 50 },
                { 40, 41, 42, 44, 45, 46, 48, 49, 50, 50, 50, 50, 50, 50 },
                { 40, 40, 41, 43, 44, 45, 46, 48, 49, 50, 50, 50, 50, 50 },
                { 39, 40, 40, 42, 43, 44, 45, 46, 48, 49, 50, 50, 50, 50 },
                { 39, 39, 40, 41, 42, 43, 44, 45, 47, 48, 49, 50, 50, 50 },
                { 38, 39, 39, 40, 41, 42, 43, 44, 46, 47, 48, 49, 50, 50 },
                { 38, 38, 39, 39, 40, 41, 42, 43, 45, 46, 47, 48, 49, 50 },
                { 37, 38, 38, 39, 39, 40, 41, 42, 44, 45, 46, 47, 48, 49 },
                { 37, 37, 38, 38, 39, 40, 40, 41, 43, 44, 45, 46, 47, 48 },
                { 36, 37, 37, 38, 38, 39, 40, 40, 42, 43, 44, 45, 46, 47 },
                { 36, 36, 37, 37, 38, 39, 39, 40, 41, 42, 43, 44, 45, 46 },
                { 35, 36, 36, 37, 37, 38, 39, 39, 40, 41, 42, 43, 44, 45 },
                { 35, 35, 36, 36, 37, 38, 38, 39, 40, 40, 41, 42, 43, 44 },
                { 34, 35, 35, 36, 36, 37, 38, 38, 39, 40, 40, 41, 42, 43 },
                { 33, 34, 35, 35, 36, 37, 37, 38, 39, 39, 40, 40, 41, 42 },
                { 32, 33, 34, 35, 35, 36, 37, 37, 38, 39, 39, 40, 40, 41 },
                { 31, 32, 33, 34, 35, 36, 36, 37, 38, 38, 39, 39, 40, 40 },
                { 30, 31, 32, 33, 34, 35, 36, 36, 37, 38, 38, 39, 39, 40 },
                { 29, 30, 31, 32, 33, 35, 35, 36, 37, 37, 38, 38, 39, 39 },
                { 28, 29, 30, 31, 32, 34, 35, 35, 36, 37, 37, 38, 38, 39 },
                { 27, 28, 29, 30, 31, 33, 35, 35, 36, 36, 37, 37, 38, 38 },
                { 26, 27, 28, 29, 30, 32, 34, 35, 35, 36, 36, 37, 37, 38 },
                { 25, 26, 27, 28, 29, 31, 33, 34, 35, 35, 36, 36, 37, 37 },
                { 24, 25, 26, 27, 28, 30, 32, 33, 35, 35, 35, 36, 36, 37 },
                { 23, 24, 25, 26, 27, 29, 31, 32, 34, 35, 35, 35, 36, 36 },
                { 22, 23, 24, 25, 26, 28, 30, 31, 33, 34, 35, 35, 35, 36 },
                { 21, 22, 23, 24, 25, 27, 29, 30, 32, 33, 34, 35, 35, 35 },
                { 20, 21, 22, 23, 24, 26, 28, 29, 31, 32, 33, 34, 35, 35 },
                { 19, 20, 21, 22, 23, 25, 27, 28, 30, 31, 32, 33, 34, 35 },
                { 18, 19, 20, 21, 22, 24, 26, 27, 29, 30, 31, 32, 33, 34 },
                { 16, 18, 19, 20, 21, 23, 25, 26, 28, 29, 30, 31, 32, 33 },
                { 14, 16, 18, 19, 20, 22, 24, 25, 27, 28, 29, 30, 31, 32 },
                { 12, 14, 16, 18, 19, 21, 23, 24, 26, 27, 28, 29, 30, 31 },
                { 10, 12, 14, 16, 18, 20, 22, 23, 25, 26, 27, 28, 29, 30 },
                { 8, 10, 12, 14, 16, 18, 20, 22, 24, 25, 26, 27, 28, 29 },
                { 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 25, 26, 27, 28 },
                { 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 25, 26, 27 },
                { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 25, 26 },
                { 1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 25 },
                { 0, 1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24 },
                { 0, 0, 1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22 },
                { 0, 0, 0, 1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20 },
                { 0, 0, 0, 0, 1, 2, 4, 6, 8, 10, 12, 14, 16, 18 },
                { 0, 0, 0, 0, 0, 1, 2, 4, 6, 8, 10, 12, 14, 16 },
                { 0, 0, 0, 0, 0, 0, 1, 2, 4, 6, 8, 10, 12, 14 },
                { 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 6, 8, 10, 12 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 6, 8, 10 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 6, 8 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 6 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
            };    

        int ageGroup = 1;    
        if (age < 22) {
            ageGroup = 1;
        } else if (age <= 60) {
            ageGroup = (int)Math.floor((age - 22) / 3) + 2;
        } else {
            ageGroup = 14;
        }

        int runCount = 60;
        if (runTime < 510) {
            runCount = 60;
        } else if (runTime > 1100) {
            runCount = 0;
        } else {
            runCount = 60 - (int)Math.floor((runTime - 500) / 10.0);
        }
        
        int runTimeScore = RunScoringTable[60 - runCount][ageGroup - 1];
        int pushUpScore = pushAndSitScoringTable[60 - pushUpCount][ageGroup - 1];
        int sitUpScore = pushAndSitScoringTable[60 - sitUpCount][ageGroup - 1];

        return pushUpScore + sitUpScore + runTimeScore;
    }


    @PostMapping("/generate-workout")
    public String generateWorkout(@Valid @ModelAttribute("video") Video video,
                                  BindingResult result,
                                  Model model,                            
                            RedirectAttributes redirectAttributes
                            ) {

        List<String> generatedVideos = youtubeService.generateYoutubeVideosId(video.getLocation(), video.getDuration(), video.getFocus());
        List<String> youtubeUrl = generatedVideos.stream()
                .map(videoId -> "https://www.youtube.com/embed/" + videoId)
                .collect(Collectors.toList());

        youtubeUrl.forEach(videoUrl -> System.out.println("Video URL: " + videoUrl));
        // Add videoUrls to the redirectAttributes
        redirectAttributes.addFlashAttribute("videoUrls", youtubeUrl);

        // Redirect to workout.html
        return "redirect:/workout";
    }
}
