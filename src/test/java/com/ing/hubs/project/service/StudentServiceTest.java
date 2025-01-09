package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.StudentCreationDto;
import com.ing.hubs.project.dto.response.StudentDto;
import com.ing.hubs.project.dto.response.StudentListDto;
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

class StudentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StudentService studentService;

    private User student;
    private StudentCreationDto studentCreationDto;
    private StudentDto studentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        student = User.builder()
                .username("Marius")
                .password("password")
                .role(Roles.STUDENT)
                .build();
        studentCreationDto = StudentCreationDto.builder()
                .username("Marius")
                .password("password")
                .email("example@example.com")
                .build();
        studentDto = StudentDto.builder()
                .username("Marius")
                .email("example@example.com")
                .build();

    }

    @Test
    void shouldCreateNewStudent() {

        when(userRepository.findByUsername("Marius")).thenReturn(Optional.empty());
        when(modelMapper.map(studentCreationDto, User.class)).thenReturn(student);
        when(userRepository.save(student)).thenReturn(student);
        when(modelMapper.map(student, StudentDto.class)).thenReturn(studentDto);

        var result = studentService.createStudent(studentCreationDto);

        assertNotNull(result);
        assertEquals("Marius", result.getUsername());
        verify(userRepository, times(1)).findByUsername("Marius");
        verify(userRepository, times(1)).save(student);
    }

    @Test
    void shouldThrowUserExists() {
        when(userRepository.findByUsername("Marius")).thenReturn(Optional.of(student));
        assertThrows(UserAlreadyExistsException.class, () -> studentService.createStudent(studentCreationDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnStudentIfExists() {
        when(userRepository.findStudentByUsername("Marius")).thenReturn(Optional.of(student));
        when(modelMapper.map(student, StudentDto.class)).thenReturn(studentDto);

        var result = studentService.getStudent("Marius");

        assertNotNull(result);
        assertEquals("Marius", result.getUsername());
        verify(userRepository, times(1)).findStudentByUsername("Marius");
    }

    @Test
    void shouldThrowIfUserDoesntExist() {
        var username = "ghostUser";

        when(userRepository.findStudentByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> studentService.getStudent(username));
        verify(userRepository, times(1)).findStudentByUsername(username);
    }

    @Test
    void ShouldReturnAllStudents() {
        var student1 = User.builder()
                .username("Marius")
                .password("password")
                .role(Roles.STUDENT)
                .build();
        var student2 = User.builder()
                .username("Marius")
                .password("password")
                .role(Roles.STUDENT)
                .build();

        var studentDto1 = new StudentDto();
        studentDto1.setUsername("student1");

        var studentDto2 = new StudentDto();
        studentDto2.setUsername("student2");

        when(userRepository.findAll()).thenReturn(List.of(student1, student2));
        when(modelMapper.map(student1, StudentDto.class)).thenReturn(studentDto1);
        when(modelMapper.map(student2, StudentDto.class)).thenReturn(studentDto2);

        var result = studentService.getAll();

        assertNotNull(result);
        assertEquals(2, result.getStudentDtoList().size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteShouldRemoveStudentIfExists() {
        when(userRepository.findStudentByUsername("Marius")).thenReturn(Optional.of(student));
        studentService.delete("Marius");
        verify(userRepository, times(1)).delete(student);
    }

    @Test
    void deleteShouldThrow() {
        var username = "ghostUser";

        when(userRepository.findStudentByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> studentService.delete(username));
        verify(userRepository, never()).delete(any(User.class));
    }
}
