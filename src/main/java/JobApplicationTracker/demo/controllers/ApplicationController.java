package JobApplicationTracker.demo.controllers;


import JobApplicationTracker.demo.entity.ApplicationStatus;
import JobApplicationTracker.demo.entity.JobApplication;
import JobApplicationTracker.demo.forms.JobApplicationForm;
import JobApplicationTracker.demo.repos.JobRepository;
import JobApplicationTracker.demo.services.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApplicationController {



    private final  ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/addApp")
    public String showForm(Model model) {
        model.addAttribute("jobApp", new JobApplicationForm());
        model.addAttribute("statuses", ApplicationStatus.values());
        return "jobApp";
    }

    @PostMapping("/addApp")
    public String addApp(@Valid @ModelAttribute("jobApp") JobApplicationForm jobApp,
                         BindingResult bindingResult,
                         Model model
                         ){
        model.addAttribute("statuses",ApplicationStatus.values());//more information about what id does and why pleas(paste it as notes)

        if (bindingResult.hasErrors()) {
            return "jobApp";
        }

        JobApplication app = applicationService.saveApplication(
                jobApp.getCompany(),
                jobApp.getJobTitle(),
                jobApp.getStatus()
        );


        model.addAttribute("app", app);
        model.addAttribute("jobApp", new JobApplicationForm());
        return "jobApp";
    }

    @GetMapping("/showApps")
    public String showApps(Model model) {
        model.addAttribute("applications", applicationService.getAllApplications());
        return "showApps";
    }


}
