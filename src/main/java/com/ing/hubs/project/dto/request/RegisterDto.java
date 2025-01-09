package com.ing.hubs.project.dto.request;

import com.ing.hubs.project.entity.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterDto {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message="password.")
    private String password;
    @Pattern(regexp = "^(?=.*\\d){8,}")
    private String phoneNumber;
    @NotNull
    private Roles role;
}
