package JobApplicationTracker.demo.controllers;


import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AddController {

    @Autowired
    UserRepository userRepo;

    @GetMapping("/add")
    public String showForm() {
        return "add";
    }

    @PostMapping("/add")
    public String addUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {
        User user = new User(email, password);
        userRepo.save(user);
        model.addAttribute("user", user);
        return "add";
    }

    @GetMapping("/show")
    public String showUsers(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "showUsers";
    }

}
