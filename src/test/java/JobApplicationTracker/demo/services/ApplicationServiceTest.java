package JobApplicationTracker.demo.services;

import JobApplicationTracker.demo.entity.Company;
import JobApplicationTracker.demo.entity.JobApplication;
import JobApplicationTracker.demo.entity.User;
import JobApplicationTracker.demo.repos.JobRepository;
import JobApplicationTracker.demo.repos.UserRepository;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private JobRepository jobRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ApplicationService applicationService;

    private User testUser;

    private Company testCompany;

    @BeforeEach
    void setUp() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);

        testUser = new User("test@example.com", "encodedPassword");
        testCompany = new Company();
        testCompany.setCompanyName("Google");
    }

    @Test
    void saveApplication() {
    }

    @Test
    void getAllApplications_shouldReturnOnlyCurrentUserApps() {
        //ARRANGE
        User anotherUser = new User("anotherUser@gmail.com", "password");

        JobApplication anotherUserApp = new JobApplication();
        anotherUserApp.setUser(anotherUser);
        anotherUserApp.setJobTitle("Fronted Developer");

        JobApplication currentUserApp1 = new JobApplication();
        currentUserApp1.setUser(testUser);
        currentUserApp1.setJobTitle("BackEnd Developer");

        JobApplication currentUserApp2 = new JobApplication();
        currentUserApp2.setUser(testUser);
        currentUserApp2.setJobTitle("DEVOPS");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jobRepo.findByUser(testUser)).thenReturn(List.of(currentUserApp1,currentUserApp2));

        //ACT
        List<JobApplication> result = applicationService.getAllApplications();

        //ASSERT
        assertThat(result).hasSize(2);
        assertThat(result).extracting(JobApplication::getUser)
                .containsOnly(testUser); // every app must belong to testUser
        assertThat(result).extracting(JobApplication::getJobTitle)
                .containsExactlyInAnyOrder("BackEnd Developer", "DEVOPS");
    }

    @Test
    void getAllApplications_shouldReturnEmptyList_whenNoApps(){
        //ARRANGE
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jobRepo.findByUser(testUser)).thenReturn(List.of());

        //ACT
        List<JobApplication> result = applicationService.getAllApplications();

        //ASSERT
        assertThat(result).isEmpty();

    }

    @Test
    void deleteApplication_shouldDelete_whenAppBelongsToCurrentUser() {
        //ARRANGE
        UUID id = UUID.randomUUID();

        JobApplication app = new JobApplication();
        app.setUser(testUser);
        app.setJobTitle("Java Developer");



        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jobRepo.findByIdAndUser(id, testUser)).thenReturn(Optional.of(app));

        //ACT
        applicationService.deleteApplication(id);
        //ASSERT
        verify(jobRepo, times(1)).delete(app);
    }

    @Test
    void deleteApplication_shouldThrow_whenAppNotFound() {
        //ARRANGE
        UUID id = UUID.randomUUID();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jobRepo.findByIdAndUser(id, testUser)).thenReturn(Optional.empty());

        //ACT + ASSERT

        assertThatThrownBy(() -> applicationService.deleteApplication(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Application not found");

    }

    @Test
    void getApplicationForCurrentUser_shouldReturnApp_whenFound() {
        //ARRANGE
        UUID id = UUID.randomUUID();

        JobApplication app = new JobApplication();
        app.setUser(testUser);
        app.setJobTitle("Java Developer");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jobRepo.findByIdAndUser(id, testUser)).thenReturn(Optional.of(app));

        //ACT
        JobApplication result = applicationService.getApplicationForCurrentUser(id);

        //ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getJobTitle()).isEqualTo("Java Developer");
        assertThat(result.getUser()).isEqualTo(testUser);

    }

    @Test
    void getApplicationForCurrentUser_shouldThrow_whenNotFound(){
        //ARRANGE
        UUID id = UUID.randomUUID();

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jobRepo.findByIdAndUser(id, testUser)).thenReturn(Optional.empty());

        //ACT + ASSERT

        assertThatThrownBy(() -> applicationService.getApplicationForCurrentUser(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Application not found");
    }

    @Test
    void updateApplication() {
    }
}