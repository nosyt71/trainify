package nus.iss.trainify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import nus.iss.trainify.model.User;
import nus.iss.trainify.model.Video;

import java.util.List;

@Controller
@RequestMapping
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";}

    @GetMapping("/RestAPI")
    public String api() {
        return "api";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/generator")
    public String generator(Model model) {
        model.addAttribute("video", new Video());
        return "generator";}

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";}

    @GetMapping("/complete")
    public String complete() {
        return "complete";}

    @GetMapping("/workout")
    public String showWorkout(Model model) {
        // Retrieve videoUrls from the model
        List<String> videoUrls = (List<String>) model.getAttribute("videoUrls");

        model.addAttribute("videoUrls", videoUrls);

        return "workout";
    }
}
