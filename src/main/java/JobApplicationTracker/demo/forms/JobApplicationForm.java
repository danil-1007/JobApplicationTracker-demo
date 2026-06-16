package JobApplicationTracker.demo.forms;

import JobApplicationTracker.demo.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobApplicationForm {

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must be at most 255 characters")
    private String company;

    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Job title must be at most 255 characters")
    private String jobTitle;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}
