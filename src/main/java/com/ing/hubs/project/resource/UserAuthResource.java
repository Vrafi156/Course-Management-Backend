package com.ing.hubs.project.resource;

import com.ing.hubs.project.dto.request.LoginDto;
import com.ing.hubs.project.dto.request.RegisterDto;
import com.ing.hubs.project.dto.response.AuthResponseDto;
import com.ing.hubs.project.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserAuthResource {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody @Valid LoginDto loginDto) {return authService.login(loginDto);}

    @PostMapping("/register")
    public AuthResponseDto register(@RequestBody @Valid RegisterDto registerDto) {return authService.register(registerDto);}

    @PostMapping("/logout")
    public void logout(){authService.logout();}
}
