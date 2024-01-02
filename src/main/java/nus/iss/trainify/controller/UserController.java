package nus.iss.trainify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nus.iss.trainify.model.User;

@Controller
@RequestMapping(path="/user")
public class UserController {

    @Qualifier("redis") // Specify the qualifier for the desired RedisTemplate bean
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "user:";


    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                                BindingResult result,
                                Model model,
                                HttpSession session) {

        if (result.hasErrors()) {
            return "register";
        }
                        
        String username = user.getUsername();
        String password = user.getPassword();

        // Check if the user already exists
        if (redisTemplate.hasKey(REDIS_KEY_PREFIX + username)) {
            result.rejectValue("username", "error.user", "User already exists");
            return "register";
        }

        // Store user information in Redis
        redisTemplate.opsForHash().put(REDIS_KEY_PREFIX + username, "username", password);
        redisTemplate.opsForHash().put(REDIS_KEY_PREFIX + username, "password", password);

        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("user") User user,
                            BindingResult result,
                            Model model,
                            HttpSession session
                            ) {

        String username = user.getUsername();
        String password = user.getPassword();

        if(result.hasErrors()){
            return "login";
        }

        // Retrieve stored password from Redis
        String storedPassword = (String) redisTemplate.opsForHash().get(REDIS_KEY_PREFIX + username, "password");
        String storedUsername = (String) redisTemplate.opsForHash().get(REDIS_KEY_PREFIX + username, "username");


        // Check if the username and password matches
        if (username.equals(storedUsername) && password.equals(storedPassword)) {
            return "redirect:/dashboard?username=" + username;
        } else {
            result.rejectValue("username", "error.user", "Invalid username or password");
            result.rejectValue("password", "error.password", "Invalid username or password");
            return "login";
        }

    }
}
