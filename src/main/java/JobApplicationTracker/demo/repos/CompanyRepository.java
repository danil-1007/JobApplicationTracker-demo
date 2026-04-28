package JobApplicationTracker.demo.repos;

import JobApplicationTracker.demo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByCompanyNameIgnoreCase(String companyName);

    List<Company> findByLocationContainingIgnoreCase(String location);

}
