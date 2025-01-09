package com.ing.hubs.project.service;

import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;
    @BeforeEach
    void beforeEach() throws NoSuchFieldException, IllegalAccessException {
        Field timeToLive = JwtService.class.getDeclaredField("timeToLive");
        Field secretKey = JwtService.class.getDeclaredField("secretKey");
        secretKey.setAccessible(true);
        timeToLive.setAccessible(true);
        // obviously if made for production, get a public key from anywhere to test, not same key
        secretKey.set(jwtService,"PoBfSaJTLTY+SGvpbm7wAUG0E1su9Gi6IfXOxo4yOiU=");
        timeToLive.set(jwtService, 60);
    }
    @Test
    void shouldGenerateJwt(){
        UserDetails userDetails = User.builder()
                .username("username")
                .role(Roles.STUDENT)
                .password("password")
                .build();
        var jwt = jwtService.generateJwt(userDetails);
        String username = jwtService.extractUsername(jwt);
        assertEquals("username",username);

    }
}
