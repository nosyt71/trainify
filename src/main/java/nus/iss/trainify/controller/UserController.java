package nus.iss.trainify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class UserController {

    @Qualifier("redis") // Specify the qualifier for the desired RedisTemplate bean
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "user:";


    @PostMapping("/user/register")
    public String registerUser(@RequestParam("newUsername") String newUsername,
                               @RequestParam("newPassword") String newPassword) {


        // Check if the user already exists
        if (redisTemplate.hasKey(REDIS_KEY_PREFIX + newUsername)) {
            return "User already exists";
        }

        // Store user information in Redis
        redisTemplate.opsForHash().put(REDIS_KEY_PREFIX + newUsername, "username", newPassword);
        redisTemplate.opsForHash().put(REDIS_KEY_PREFIX + newUsername, "password", newPassword);

        return "redirect:/login";
    }

    @PostMapping("/user/login")
    public String loginUser(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            RedirectAttributes attributes) {

        // Check if the user exists
        if (!redisTemplate.hasKey(REDIS_KEY_PREFIX + username)) {
            attributes.addFlashAttribute("error", "User does not exist");
            return "redirect:/login";
        }

        // Retrieve stored password from Redis
        String storedPassword = (String) redisTemplate.opsForHash().get(REDIS_KEY_PREFIX + username, "password");

        // Check if the provided password matches the stored password
        if (password.equals(storedPassword)) {
            return "redirect:/dashboard?username=" + username;
        } else {
            attributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }
}
