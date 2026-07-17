package JobApplicationTracker.demo.controllers;


import JobApplicationTracker.demo.entity.ApplicationStatus;
import JobApplicationTracker.demo.entity.JobApplication;
import JobApplicationTracker.demo.forms.JobApplicationForm;
import JobApplicationTracker.demo.repos.JobRepository;
import JobApplicationTracker.demo.services.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class ApplicationController {



    private final ApplicationService applicationService;

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
        // Re-populate the status dropdown so the view can render even when validation fails.
        model.addAttribute("statuses", ApplicationStatus.values());

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


    @PostMapping("/applications/{id}/delete")
    public String deleteApp(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
        return "redirect:/showApps";
    }


    @GetMapping("/applications/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        JobApplication app = applicationService.getApplicationForCurrentUser(id);

        JobApplicationForm form = new JobApplicationForm();
        form.setCompany(app.getCompany().getCompanyName());
        form.setJobTitle(app.getJobTitle());
        form.setStatus(app.getStatus());

        model.addAttribute("jobApp", form);
        model.addAttribute("applicationId", id);
        model.addAttribute("statuses", ApplicationStatus.values());
        return "editApp";
    }

    @PostMapping("/applications/{id}/edit")
    public String updateApp(@PathVariable UUID id,
                            @Valid @ModelAttribute("jobApp") JobApplicationForm jobApp,
                            BindingResult bindingResult,
                            Model model) {
        model.addAttribute("applicationId", id);
        model.addAttribute("statuses", ApplicationStatus.values());

        if (bindingResult.hasErrors()) {
            return "editApp";
        }

        applicationService.updateApplication(
                id,
                jobApp.getCompany(),
                jobApp.getJobTitle(),
                jobApp.getStatus()
        );

        return "redirect:/showApps";
    }


}
