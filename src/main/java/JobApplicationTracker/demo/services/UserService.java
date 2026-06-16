package JobApplicationTracker.demo.services;

import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.repos.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User saveUser(String email, String password) {
        String trimmedEmail = email == null ? "" : email.trim();

        if(trimmedEmail.isBlank())
            throw new IllegalArgumentException("Email should not be empty");

        if(userRepo.findByEmail(trimmedEmail).isPresent())
            throw new IllegalArgumentException("Email already exists");

        User user = new User(trimmedEmail, passwordEncoder.encode(password));

        return userRepo.save(user);
    }

    /**3. @Transactional on getAllUsers()
     Not wrong, but unnecessary for a single findAll().
     Keep @Transactional on saveUser (and on multi-step methods in ApplicationService).
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

}
