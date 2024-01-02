package nus.iss.trainify.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nus.iss.trainify.model.Workout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping
public class IPPTRestController {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Qualifier("redis") // Specify the qualifier for the desired RedisTemplate bean
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "workout:";
    
    @GetMapping(path = "/api/Trainify/{username}", produces = "application/json")
    public ResponseEntity<String> getWorkoutList(@PathVariable("username") String username) {
        String key = REDIS_KEY_PREFIX + username;

        ListOperations<String, String> listOperations = redisTemplate.opsForList();

        if (listOperations != null) {
            // Retrieve the list of workout maps from Redis
            List<String> workoutStrings = listOperations.range(key, 0, -1);

            // Convert the list of strings to a list of Workout objects
            List<Workout> userWorkouts = workoutStrings.stream()
                    .map(this::deserializeWorkoutString)
                    .collect(Collectors.toList());

            // Print all userWorkouts
            for (Workout workout : userWorkouts) {
                System.out.println(workout);
            }
            System.out.println(workoutStrings.toString());
            int streak = calculateStreak(userWorkouts);

            // Create a JSON response
            String response = "{\"username\":\"" + username + "\", \"streak\":" + streak + ", \"workouts\":" + workoutStrings.toString() + "}";

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Failed to retrieve workouts");
        }
    }

    @GetMapping(path = "/api/Trainify", produces = "application/json")
    public ResponseEntity<String> getAllWorkoutList() {
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");

        List<Workout> allWorkouts = new ArrayList<>();

        for (String key : keys) {
            ListOperations<String, String> listOperations = redisTemplate.opsForList();

            if (listOperations != null) {
                // Retrieve the list of workout maps from Redis
                List<String> workoutStrings = listOperations.range(key, 0, -1);

                // Convert the list of strings to a list of Workout objects
                List<Workout> userWorkouts = workoutStrings.stream()
                        .map(this::deserializeWorkoutString)
                        .collect(Collectors.toList());

                allWorkouts.addAll(userWorkouts);
            }
        }

        // Print all userWorkouts
        for (Workout workout : allWorkouts) {
            System.out.println(workout);
        }

        int streak = calculateStreak(allWorkouts);

        // Create a JSON response
        String response = "{\"workouts\":" + allWorkouts.toString() + ", \"streak\":" + streak + "}";

        return ResponseEntity.ok(response);
    }

    // Deserialize Workout string to Workout object
    private Workout deserializeWorkoutString(String workoutString) {
        try {
            return objectMapper.readValue(workoutString, Workout.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize Workout string", e);
        }
    }
    
    // calculate streak based on times of IPPT results logged
    private int calculateStreak(List<Workout> workouts) {
        
        int streak = 0;
        for (Workout workout : workouts) {
            if (workout.isCompleted()) {
                streak++;
            } else {
                break; // Streak is broken if a workout not completed after a number of days etc. for e.g. 15 days
            }
        }
        return streak;
    }
}
