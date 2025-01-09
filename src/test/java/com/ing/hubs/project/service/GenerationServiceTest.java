package com.ing.hubs.project.service;

import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.Schedule;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.repository.CourseGradeRepository;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.RequestRepository;
import com.ing.hubs.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private CourseGradeRepository courseGradeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private User teacher;
    @InjectMocks
    private GenerationService generationService;

    @BeforeEach
    void beforeEach(){
        teacher = User.builder()
                .username("teacher1")
                .role(Roles.TEACHER)
                .build();
    }
    @Test
    void shouldPopulateWithStudents() {
        Integer size = generationService.populateWithUsers();
        verify(userRepository,times(2)).saveAll(anyCollection());
        assertEquals(55,size);
    }

    @Test
    void shouldPopulateWithCourses() {

        when(userRepository.findTeacherByUsername(anyString())).thenReturn(Optional.ofNullable(teacher));

        Integer size = generationService.populateWithCourses();

        assertEquals(15,size);
    }

    @Test
    void testAddStudentsToCourses() {

        Course course = Course.builder()
                .name("Wizardry")
                .teacher(teacher)
                .maxAttendees(20)
                .currentAttendees(0)
                .build();


        when(userRepository.findTeacherByUsername("teacher1")).thenReturn(Optional.ofNullable(teacher));
        when(courseRepository.findByNameAndTeacher("Wizardry", teacher)).thenReturn(Optional.ofNullable(course));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(teacher));

        List<Integer> results = generationService.addStudentsToCourses();

        assertEquals(50, results.get(0));
        assertEquals(19, results.get(1));
    }

}
