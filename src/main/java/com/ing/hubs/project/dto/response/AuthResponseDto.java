package com.ing.hubs.project.dto.response;

import com.ing.hubs.project.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDto {
    private String username;
    private Roles role;
    private String token;
}
