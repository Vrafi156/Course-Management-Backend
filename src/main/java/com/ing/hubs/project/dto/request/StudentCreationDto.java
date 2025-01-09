package com.ing.hubs.project.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCreationDto {
    @NotBlank
    private String email;
    @NotBlank
    private String username;
    @NotBlank
    private String password;


}


