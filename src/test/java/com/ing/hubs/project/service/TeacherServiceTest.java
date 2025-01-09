package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.TeacherCreationDto;
import com.ing.hubs.project.dto.response.TeacherDto;
import com.ing.hubs.project.dto.response.TeacherListDto;
import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.exception.UserAlreadyExistsException;
import com.ing.hubs.project.exception.UserNotFoundException;
import com.ing.hubs.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TeacherService teacherService;

    private User teacher;
    private TeacherCreationDto teacherCreationDto;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        teacher = User.builder()
                .username("JohnDoe")
                .password("password")
                .role(Roles.TEACHER)
                .build();

        teacherCreationDto = TeacherCreationDto.builder()
                .username("JohnDoe")
                .password("password")
                .email("johndoe@example.com")
                .build();

        teacherDto = TeacherDto.builder()
                .username("JohnDoe")
                .email("johndoe@example.com")
                .build();
    }

    @Test
    void shouldCreateNewTeacher() {
        when(userRepository.findByUsername("JohnDoe")).thenReturn(Optional.empty());
        when(modelMapper.map(teacherCreationDto, User.class)).thenReturn(teacher);
        when(userRepository.save(teacher)).thenReturn(teacher);
        when(modelMapper.map(teacher, TeacherDto.class)).thenReturn(teacherDto);

        var result = teacherService.createTeacher(teacherCreationDto);

        assertNotNull(result);
        assertEquals("JohnDoe", result.getUsername());
        verify(userRepository, times(1)).findByUsername("JohnDoe");
        verify(userRepository, times(1)).save(teacher);
    }

    @Test
    void shouldThrowUserAlreadyExistsException() {
        when(userRepository.findByUsername("JohnDoe")).thenReturn(Optional.of(teacher));

        assertThrows(UserAlreadyExistsException.class, () -> teacherService.createTeacher(teacherCreationDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnTeacherIfExists() {
        when(userRepository.findTeacherByUsername("JohnDoe")).thenReturn(Optional.of(teacher));
        when(modelMapper.map(teacher, TeacherDto.class)).thenReturn(teacherDto);

        var result = teacherService.getTeacher("JohnDoe");

        assertNotNull(result);
        assertEquals("JohnDoe", result.getUsername());
        verify(userRepository, times(1)).findTeacherByUsername("JohnDoe");
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenTeacherNotExists() {
        when(userRepository.findTeacherByUsername("UnknownTeacher")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> teacherService.getTeacher("UnknownTeacher"));
        verify(userRepository, times(1)).findTeacherByUsername("UnknownTeacher");
    }

    @Test
    void shouldReturnAllTeachers() {
        var teacher1 = User.builder()
                .username("Teacher1")
                .password("password")
                .role(Roles.TEACHER)
                .build();

        var teacher2 = User.builder()
                .username("Teacher2")
                .password("password")
                .role(Roles.TEACHER)
                .build();

        var teacherDto1 = TeacherDto.builder().username("Teacher1").build();
        var teacherDto2 = TeacherDto.builder().username("Teacher2").build();

        when(userRepository.findAll()).thenReturn(List.of(teacher1, teacher2));
        when(modelMapper.map(teacher1, TeacherDto.class)).thenReturn(teacherDto1);
        when(modelMapper.map(teacher2, TeacherDto.class)).thenReturn(teacherDto2);

        var result = teacherService.getAll();

        assertNotNull(result);
        assertEquals(2, result.getTeacherDtoList().size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteShouldRemoveTeacherIfExists() {
        when(userRepository.findTeacherByUsername("JohnDoe")).thenReturn(Optional.of(teacher));

        teacherService.delete("JohnDoe");

        verify(userRepository, times(1)).delete(teacher);
    }

    @Test
    void deleteShouldThrowUserNotFoundException() {
        when(userRepository.findTeacherByUsername("Batman")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> teacherService.delete("UnknownTeacher"));
        verify(userRepository, never()).delete(any(User.class));
    }
}
