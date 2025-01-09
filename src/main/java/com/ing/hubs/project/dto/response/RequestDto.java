package com.ing.hubs.project.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.RequestStatus;
import com.ing.hubs.project.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto {
    private int id;
    private LocalDate timeSent;
    private RequestStatus requestStatus;
    private String studentName;
    private String course_name;
}
