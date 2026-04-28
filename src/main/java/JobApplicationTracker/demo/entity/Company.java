package JobApplicationTracker.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy =jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @Column(name = "company_name")
    private String companyName;
    private String website;
    private String location;

    @OneToMany(mappedBy = "company")
    private List<JobApplication> applications;

}
