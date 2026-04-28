package JobApplicationTracker.demo.repos;

import JobApplicationTracker.demo.entity.ApplicationStatus;
import JobApplicationTracker.demo.entity.JobApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<JobApplication, UUID>{

    List<JobApplication> findAllByStatus(ApplicationStatus status);

    /** Loads user + company in one query so the list page can read names without lazy-load errors. */
    @EntityGraph(attributePaths = {"user", "company"})
    @Query("SELECT j FROM JobApplication j")
    List<JobApplication> findAllWithUserAndCompany();
}
