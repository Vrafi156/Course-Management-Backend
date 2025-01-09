package com.ing.hubs.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FeedbackCreationDto {
    @NotBlank
    private String message;

    @NotBlank
    private String course_name;

    private String course_teacher;



}
