package nus.iss.trainify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String loginPage(@ModelAttribute("error") String error, Model model) {
        // Use 'error' attribute as needed
        model.addAttribute("error", error);
        return "login";
    }

    @GetMapping("/generator")
    public String generator() {
        return "generator";}

    @GetMapping("/register")
    public String register() {
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
