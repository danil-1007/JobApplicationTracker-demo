package JobApplicationTracker.demo.services;

import JobApplicationTracker.demo.entity.ApplicationStatus;
import JobApplicationTracker.demo.entity.Company;
import JobApplicationTracker.demo.entity.JobApplication;
import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.repos.CompanyRepository;
import JobApplicationTracker.demo.repos.JobRepository;
import JobApplicationTracker.demo.repos.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {


    private final JobRepository jobRepo;

    private final UserRepository userRepo;


    private final  CompanyRepository companyRepo;

    public ApplicationService(JobRepository jobRepo,
                              UserRepository userRepo,
                              CompanyRepository companyRepo) {
        this.jobRepo = jobRepo;
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
    }

    /**
     *3. Find-or-create company without @Transactional
     * Inside one save you may:
     * 1.
     * read company
     * 2.
     * insert company
     * 3.
     * insert job application
     *
     * Why it matters: If step 3 fails after step 2, you can leave an orphan company.
     * Unlikely in a demo, but @Transactional on saveApplication is the right habit.
     */
    @Transactional
    public JobApplication saveApplication(String companyName, String jobTitle, ApplicationStatus status) {


            User user = getCurrentUser();

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

            return jobRepo.save(app);
    }

    public List<JobApplication> getAllApplications() {
        return jobRepo.findByUser(getCurrentUser());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Signed-in user not found"));
    }

    public void deleteApplication(UUID id) {
        JobApplication app = jobRepo.findByIdAndUser(id, getCurrentUser())
                        .orElseThrow(() ->new IllegalArgumentException("Application not found"));
        jobRepo.delete(app);
    }


    public JobApplication getApplicationForCurrentUser(UUID id) {
        return jobRepo.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() ->new IllegalArgumentException("Application not found"));
    }

    @Transactional
    public JobApplication updateApplication(UUID id, String companyName, String jobTitle, ApplicationStatus status) {
        JobApplication app = getApplicationForCurrentUser(id);

        String name = companyName.trim();
        Company company = companyRepo.findByCompanyNameIgnoreCase(name)
                .orElseGet(() -> {
                    Company c = new Company();
                    c.setCompanyName(name);
                    return companyRepo.save(c);
                });

        app.setCompany(company);
        app.setJobTitle(jobTitle.trim());
        app.setStatus(status);

        return jobRepo.save(app);

    }
}
