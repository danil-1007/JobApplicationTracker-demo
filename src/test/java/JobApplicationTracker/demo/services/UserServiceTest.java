package JobApplicationTracker.demo.services;


import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Matches your User(trimmedEmail, encodedPassword) constructor
        testUser = new User("test@example.com", "encodedPassword");
    }

    // ----------------------------------------------------------------
    // saveUser() tests
    // ----------------------------------------------------------------

    @Test
    void saveUser_shouldEncodePasswordAndSave() {
        // ARRANGE
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        // ACT
        User result = userService.saveUser("test@example.com", "plainPassword");

        // ASSERT
        assertThat(result).isNotNull();
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_shouldTrimEmailBeforeSaving() {
        // ARRANGE — email with spaces should be trimmed to "test@example.com"
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        // ACT
        User result = userService.saveUser("  test@example.com  ", "plainPassword");

        // ASSERT — repo was called with trimmed email, not the spaced version
        assertThat(result).isNotNull();
        verify(userRepo).findByEmail("test@example.com"); // trimmed!
    }

    @Test
    void saveUser_shouldThrow_whenEmailIsBlank() {
        // ACT + ASSERT — blank email should throw IllegalArgumentException
        assertThatThrownBy(() -> userService.saveUser("   ", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email should not be empty");

        // ASSERT — repo should never be called
        verifyNoInteractions(userRepo);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void saveUser_shouldThrow_whenEmailAlreadyExists() {
        // ARRANGE — simulate email already in DB
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // ACT + ASSERT
        assertThatThrownBy(() -> userService.saveUser("test@example.com", "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already exists");

        // ASSERT — save should never be called
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void saveUser_shouldThrow_whenEmailIsNull() {
        // ACT + ASSERT — null email trims to "" which is blank → should throw
        assertThatThrownBy(() -> userService.saveUser(null, "password"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email should not be empty");
    }

    // ----------------------------------------------------------------
    // getAllUsers() tests
    // ----------------------------------------------------------------

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // ARRANGE
        User anotherUser = new User("other@example.com", "encodedPassword");
        when(userRepo.findAll()).thenReturn(List.of(testUser, anotherUser));

        // ACT
        List<User> result = userService.getAllUsers();

        // ASSERT
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getEmail)
                .containsExactlyInAnyOrder("test@example.com", "other@example.com");
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() {
        // ARRANGE
        when(userRepo.findAll()).thenReturn(List.of());

        // ACT
        List<User> result = userService.getAllUsers();

        // ASSERT
        assertThat(result).isEmpty();
    }
}