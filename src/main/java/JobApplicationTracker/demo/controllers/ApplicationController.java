package JobApplicationTracker.demo.controllers;


import JobApplicationTracker.demo.entity.ApplicationStatus;
import JobApplicationTracker.demo.entity.Company;
import JobApplicationTracker.demo.entity.JobApplication;
import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.repos.CompanyRepository;
import JobApplicationTracker.demo.repos.JobRepository;
import JobApplicationTracker.demo.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ApplicationController {

    @Autowired
    JobRepository jobRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @GetMapping("/addApp")
    public String showForm(Model model) {
        model.addAttribute("statuses", ApplicationStatus.values());
        return "jobApp";
    }

    @PostMapping("/addApp")
    public String addApp(@RequestParam("company") String companyName,
                         @RequestParam("jobTitle") String jobTitle,
                         @RequestParam("status")ApplicationStatus status,
                         Model model
                         ){
        model.addAttribute("statuses",ApplicationStatus.values());//more information about what id does and why pleas(paste it as notes)

        User user = userRepo.findAll().stream()
                        .findFirst()
                                .orElseThrow(() -> new IllegalStateException("No users in DB"));//more information about what id does and why pleas(paste it as notes)

        String name = companyName.trim();
        /**
         * same about next par of code(I mean company), give more info(same in notes)
         */
        Company company = companyRepo.findByCompanyNameIgnoreCase(name)
                        .orElseGet(()->{
                            Company c = new Company();
                            c.setCompanyName(name);
                            return companyRepo.save(c);
                        });

        JobApplication app = new JobApplication();
        app.setUser(user);
        app.setCompany(company);
        app.setJobTitle(jobTitle.trim());
        app.setStatus(status);

        jobRepo.save(app);


        model.addAttribute("app", app);

        return "jobApp";
    }

    @GetMapping("/showApps")
    public String showApps(Model model) {
        model.addAttribute("applications", jobRepo.findAllWithUserAndCompany());
        return "showApps";
    }


}
