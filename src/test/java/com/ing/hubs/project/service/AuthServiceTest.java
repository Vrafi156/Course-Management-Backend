package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.LoginDto;
import com.ing.hubs.project.dto.request.RegisterDto;
import com.ing.hubs.project.dto.response.AuthResponseDto;
import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.exception.InvalidLoginInfoException;
import com.ing.hubs.project.exception.UserAlreadyExistsException;
import com.ing.hubs.project.exception.UserNotFoundException;
import com.ing.hubs.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private  AuthenticationManager authenticationManager;
    @Mock
    private  ModelMapper modelMapper;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterDto registerDto;
    private LoginDto loginDto;

    @BeforeEach
    void beforeEach(){
        SecurityContextHolder.clearContext();
        user = User.builder()
                .username("user")
                .password("password")
                .role(Roles.STUDENT)
                .build();
        registerDto = RegisterDto.builder()
                .username("user")
                .email("email")
                .password("password")
                .role(Roles.STUDENT)
                .build();
        loginDto = new LoginDto("user","password");
    }




    @Test
    void userShouldAlreadyExistAtRegister(){
        when(userRepository.findByUsername(registerDto.getUsername())).thenReturn(Optional.ofNullable(user));

        assertThrows(UserAlreadyExistsException.class,()->authService.register(registerDto));

    }

    @Test
    void shouldRegisterSuccessfully(){
        when(userRepository.findByUsername(registerDto.getUsername())).thenReturn(Optional.empty());
        when(modelMapper.map(registerDto,User.class)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded");
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateJwt(user)).thenReturn("token");
        AuthResponseDto authResponseDto = authService.register(registerDto);
        assertNotNull(authResponseDto);
        assertEquals("user",authResponseDto.getUsername());
        assertEquals(Roles.STUDENT,authResponseDto.getRole());
        assertEquals("token",authResponseDto.getToken());

    }

    @Test
    void userShouldNotExistAtLogin(){
        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,()->authService.login(loginDto));

    }

    @Test
    void shouldHaveWrongPassword(){
        loginDto.setPassword("wrongPassword");
        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Optional.ofNullable(user));
        assertThrows(UserNotFoundException.class,()->authService.login(loginDto));
    }

    @Test
    void shouldFailToAuthenticate(){
        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        //TODO ask why InvalidLoginInfoException::new doesn't work
        when(authenticationManager.authenticate(any())).thenThrow(new InvalidLoginInfoException());
        assertThrows(InvalidLoginInfoException.class,()->authService.login(loginDto));
    }

    @Test
    void shouldLoginUser(){
        when(userRepository.findByUsername(loginDto.getUsername())).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtService.generateJwt(user)).thenReturn("token");

        AuthResponseDto response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals("user", response.getUsername());
        assertEquals(Roles.STUDENT, response.getRole());
        assertEquals("token", response.getToken());
    }

    @Test
    void shouldLogoutUser(){
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        authService.logout();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


}
