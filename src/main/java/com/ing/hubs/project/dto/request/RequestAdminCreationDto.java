package com.ing.hubs.project.dto.request;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ing.hubs.project.entity.RequestStatus;
import com.ing.hubs.project.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAdminCreationDto {
    @NotBlank
    private String field;
    @NotBlank
    private String value;

}
