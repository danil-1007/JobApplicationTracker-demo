package JobApplicationTracker.demo.controllers;


import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.forms.SignupForm;
import JobApplicationTracker.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class AddController {

    private final UserService userService;

    public AddController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("signup", new SignupForm());
        return "add";
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("signup") SignupForm signup,
                          BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "add";
        }

        try {
            User user = userService.saveUser(signup.getEmail(), signup.getPassword());
            model.addAttribute("user", user);
            model.addAttribute("signup", new SignupForm());
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("email", "email.duplicate", ex.getMessage());
            return "add";
        }

        return "add";
    }

    @GetMapping("/show")
    public String showUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "showUsers";
    }

    @PostMapping("/users/{id}/promote")
    public String promoteUser(@PathVariable UUID id) {
        userService.promoteToAdmin(id);
        return "redirect:/show";
    }

}
