package com.ing.hubs.project.service;

import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnSecurityUser(){
        User user = User.builder()
                .username("user")
                .password("password")
                .role(Roles.STUDENT)
                .build();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_STUDENT");
        when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
        var userDetails = userService.loadUserByUsername("user");
        assertEquals("user",userDetails.getUsername());
        assertEquals("password",userDetails.getPassword());
        assertEquals(Set.of(authority),userDetails.getAuthorities());
    }
}
