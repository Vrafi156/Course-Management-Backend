package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.RequestAdminCreationDto;
import com.ing.hubs.project.dto.response.RequestAdminResponseDto;
import com.ing.hubs.project.entity.RequestAdmin;
import com.ing.hubs.project.entity.RequestStatus;
import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.exception.RequestAlreadyAnsweredException;
import com.ing.hubs.project.exception.UnsuportedFieldException;
import com.ing.hubs.project.repository.RequestAdminRepository;
import com.ing.hubs.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestAdminServiceTest {

    @Mock
    private RequestAdminRepository requestAdminRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RequestAdminService requestAdminService;

    private User user;
    private RequestAdmin requestAdmin;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .username("admin")
                .email("admin@example.com")
                .role(Roles.valueOf("ADMIN"))
                .build();

        requestAdmin = RequestAdmin.builder()
                .id(2)
                .field("email")
                .value("marius@example.com")
                .timeSend(LocalDateTime.now())
                .requestStatus(RequestStatus.PENDING)
                .user(user)
                .build();
    }
    private void mockAuthentication(String username) {
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .role(Roles.ADMIN   )
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



    @Test
    void shouldCreateRequest() {
        RequestAdminCreationDto requestAdminDto = new RequestAdminCreationDto("email", "new_email@example.com");

        when(userRepository.findByUsername("Bruce")).thenReturn(Optional.of(user));
        when(requestAdminRepository.save(any(RequestAdmin.class))).thenReturn(requestAdmin);
        when(modelMapper.map(any(), eq(RequestAdminCreationDto.class))).thenReturn(requestAdminDto);

        var result = requestAdminService.createRequest(requestAdminDto, "Bruce");

        assertNotNull(result);
        assertEquals("email", result.getField());
        assertEquals("new_email@example.com", result.getValue());
        verify(requestAdminRepository, times(1)).save(any(RequestAdmin.class));
    }

    @Test
    void shouldThrowUnsuportedFieldException() {
        RequestAdminCreationDto invalidDto = new RequestAdminCreationDto("invalidField", "value");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        assertThrows(UnsuportedFieldException.class, () -> requestAdminService.createRequest(invalidDto, "admin"));
        verify(requestAdminRepository, never()).save(any(RequestAdmin.class));
    }

    @Test
    void shouldGetAllRequests() {
        when(requestAdminRepository.findAll()).thenReturn(List.of(requestAdmin));

        var response = requestAdminService.getAllRequests();

        assertNotNull(response);
        assertEquals(1, response.getRequests().size());
        assertEquals("email", response.getRequests().get(0).getFieldName());
        verify(requestAdminRepository, times(1)).findAll();
    }

    @Test
    void shouldRespondToRequest() {
        RequestAdminResponseDto responseDto = RequestAdminResponseDto.builder()
                .id(1)
                .fieldName("email")
                .newValue("new_email@example.com")
                .requestStatus(RequestStatus.CONFIRMATION)
                .build();

        when(requestAdminRepository.findById(1)).thenReturn(Optional.of(requestAdmin));
        when(modelMapper.map(any(), eq(RequestAdminResponseDto.class))).thenReturn(responseDto);

        var result = requestAdminService.respondToRequest(1, responseDto);

        assertNotNull(result);
        assertEquals("email", result.getFieldName());
        assertEquals(RequestStatus.CONFIRMATION, result.getRequestStatus());
        verify(requestAdminRepository, times(1)).save(requestAdmin);
    }

    @Test
    void shouldThrowRequestAlreadyAnsweredException() {
        requestAdmin.setRequestStatus(RequestStatus.CONFIRMATION);

        when(requestAdminRepository.findById(1)).thenReturn(Optional.of(requestAdmin));

        assertThrows(RequestAlreadyAnsweredException.class, () -> requestAdminService.respondToRequest(1, new RequestAdminResponseDto()));
        verify(requestAdminRepository, never()).save(any());
    }

    @Test
    void shouldDeleteRequest() {
        requestAdminService.delete(1);

        verify(requestAdminRepository, times(1)).deleteById(1);
    }

    @Test
    void shouldGetUserRequest() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(requestAdminRepository.findByUser(user)).thenReturn(new ArrayList<>(List.of(requestAdmin)));
        var response = requestAdminService.getUserRequest("admin");

        assertNotNull(response);
        assertEquals(1, response.getRequests().size());
        assertEquals("email", response.getRequests().get(0).getFieldName());
        verify(requestAdminRepository, times(1)).findByUser(user);
    }




    @Test
    void shouldApplyPatch() {
        RequestAdmin acceptedRequest = RequestAdmin.builder()
                .field("email")
                .value("updated_email@example.com")
                .requestStatus(RequestStatus.ACCEPTED)
                .user(user)
                .build();

        when(requestAdminRepository.findAllByRequestStatus(RequestStatus.ACCEPTED))
                .thenReturn(new ArrayList<>(List.of(acceptedRequest)));

        requestAdminService.applyPatch("admin");

        verify(userRepository, times(1)).save(user);
        verify(requestAdminRepository, times(1)).save(acceptedRequest);
        assertEquals("updated_email@example.com", user.getEmail());
        assertEquals(RequestStatus.SOLVED, acceptedRequest.getRequestStatus());
    }
}
