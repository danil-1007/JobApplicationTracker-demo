package JobApplicationTracker.demo.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy =jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    private String email;


    @NotBlank
    @Size(min = 8, max = 64, message = "Password must be 8–64 characters")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<JobApplication> applications;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
