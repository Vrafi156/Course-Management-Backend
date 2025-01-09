package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.LoginDto;
import com.ing.hubs.project.dto.request.RegisterDto;
import com.ing.hubs.project.dto.response.AuthResponseDto;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.exception.InvalidLoginInfoException;
import com.ing.hubs.project.exception.UserAlreadyExistsException;
import com.ing.hubs.project.exception.UserNotFoundException;
import com.ing.hubs.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    private User loadUser(String username){
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }
    public AuthResponseDto register(RegisterDto registerDto){
        if(userRepository.findByUsername(registerDto.getUsername()).isPresent()){
            throw new UserAlreadyExistsException();
        };
        var user = modelMapper.map(registerDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String token = jwtService.generateJwt(user);
        return AuthResponseDto.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }

    private User checkLoginDetails(LoginDto loginDto){
        var user = loadUser(loginDto.getUsername());
        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new UserNotFoundException();
        }
        return user;
    }

    private void authenticate(LoginDto loginDto){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()
                    ));
        }catch(Exception e){
            throw new InvalidLoginInfoException();
        }
    }

    public AuthResponseDto login(LoginDto loginDto){
        var user = checkLoginDetails(loginDto);
        authenticate(loginDto);
        String token = jwtService.generateJwt(user);
        return AuthResponseDto.builder()
                .username(loginDto.getUsername())
                .role(user.getRole())
                .token(token)
                .build();
    }

    public void logout(){
        SecurityContextHolder.clearContext();
    }

}
