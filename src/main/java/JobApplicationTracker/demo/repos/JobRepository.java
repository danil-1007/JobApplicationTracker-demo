package JobApplicationTracker.demo.repos;

import JobApplicationTracker.demo.entity.ApplicationStatus;
import JobApplicationTracker.demo.entity.JobApplication;
import JobApplicationTracker.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<JobApplication, UUID>{

    List<JobApplication> findAllByStatus(ApplicationStatus status);

    /** Loads user + company in one query so the list page can read names without lazy-load errors. */
    @EntityGraph(attributePaths = {"user", "company"})
    @Query("SELECT j FROM JobApplication j")
    List<JobApplication> findAllWithUserAndCompany();

    @EntityGraph(attributePaths = {"user", "company"})
    List<JobApplication> findByUser(User user);

    Optional<JobApplication> findByIdAndUser(UUID id, User user);
}
