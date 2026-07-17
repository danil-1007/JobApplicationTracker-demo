package JobApplicationTracker.demo.services;

import JobApplicationTracker.demo.entity.ApplicationStatus;
import JobApplicationTracker.demo.entity.Company;
import JobApplicationTracker.demo.entity.JobApplication;
import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.exception.ResourceNotFoundException;
import JobApplicationTracker.demo.repos.CompanyRepository;
import JobApplicationTracker.demo.repos.JobRepository;
import JobApplicationTracker.demo.repos.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final JobRepository jobRepo;
    private final UserRepository userRepo;
    private final CompanyRepository companyRepo;

    public ApplicationService(JobRepository jobRepo,
                              UserRepository userRepo,
                              CompanyRepository companyRepo) {
        this.jobRepo = jobRepo;
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
    }

    @Transactional
    public JobApplication saveApplication(String companyName, String jobTitle, ApplicationStatus status) {
        JobApplication app = new JobApplication();
        app.setUser(getCurrentUser());
        app.setCompany(findOrCreateCompany(companyName));
        app.setJobTitle(jobTitle.trim());
        app.setStatus(status);
        app.setAppliedOn(LocalDate.now());
        return jobRepo.save(app);
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getAllApplications() {
        return jobRepo.findByUser(getCurrentUser());
    }

    @Transactional(readOnly = true)
    public JobApplication getApplicationForCurrentUser(UUID id) {
        return jobRepo.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    @Transactional
    public void deleteApplication(UUID id) {
        JobApplication app = jobRepo.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        jobRepo.delete(app);
    }

    @Transactional
    public JobApplication updateApplication(UUID id, String companyName, String jobTitle, ApplicationStatus status) {
        JobApplication app = getApplicationForCurrentUser(id);
        app.setCompany(findOrCreateCompany(companyName));
        app.setJobTitle(jobTitle.trim());
        app.setStatus(status);
        return jobRepo.save(app);
    }

    /** Returns the existing company matching this name (case-insensitive), creating it if absent. */
    private Company findOrCreateCompany(String companyName) {
        String name = companyName.trim();
        return companyRepo.findByCompanyNameIgnoreCase(name)
                .orElseGet(() -> {
                    Company company = new Company();
                    company.setCompanyName(name);
                    return companyRepo.save(company);
                });
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Signed-in user not found"));
    }
}
