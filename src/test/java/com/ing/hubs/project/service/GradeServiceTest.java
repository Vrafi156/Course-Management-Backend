package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.GradeAssignDto;
import com.ing.hubs.project.dto.response.GradeDto;
import com.ing.hubs.project.dto.response.GradeListDto;
import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.CourseGrade;
import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.exception.CourseGradeNotFoundException;
import com.ing.hubs.project.exception.CourseNotFoundException;
import com.ing.hubs.project.exception.UserNotFoundException;
import com.ing.hubs.project.repository.CourseGradeRepository;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private CourseGradeRepository courseGradeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private GradeService gradeService;

    private User user;

    private Course course;

    private CourseGrade courseGrade;

    @BeforeEach
    void beforeEach() {
        SecurityContextHolder.clearContext();
        user = User.builder()
                .username("student")
                .build();
        course = Course.builder()
                .name("course")
                .build();
        courseGrade=CourseGrade.builder()
                .course(course)
                .student(user)
                .grade(5)
                .build();
    }

    private void mockAuthentication(String username) {
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .role(Roles.STUDENT)
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldReturnGradesForAuthenticatedStudent() {
        mockAuthentication(user.getUsername());
        when(courseGradeRepository.findAll()).thenReturn(List.of(courseGrade));

        GradeListDto gradeListDto = gradeService.getMyGrades();

        assertEquals(1,gradeListDto.getGrades().size());
        assertEquals("student",gradeListDto.getGrades().get(0).getStudentUsername());
        assertEquals("course",gradeListDto.getGrades().get(0).getCourseName());
        assertEquals(5,gradeListDto.getGrades().get(0).getGrade());
    }

    @Test
    void shouldReturnEnrolledStudents(){
        String teacherUsername = "teacher";
        mockAuthentication(teacherUsername);
        var teacher = User.builder().username("teacher").build();
        when(userRepository.findTeacherByUsername(teacherUsername)).thenReturn(Optional.ofNullable(teacher));
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.ofNullable(course));
        when(courseGradeRepository.findAll()).thenReturn(List.of(courseGrade));

        GradeListDto gradeListDto = gradeService.getEnrolledStudents("course");

        assertEquals(1,gradeListDto.getGrades().size());
        assertEquals("course",gradeListDto.getGrades().get(0).getCourseName());
        assertEquals("student",gradeListDto.getGrades().get(0).getStudentUsername());
        assertEquals(5,gradeListDto.getGrades().get(0).getGrade());
    }

    @Test
    void shouldReturnGradesForStudent() {
        mockAuthentication("teacher");
        when(courseGradeRepository.findAll()).thenReturn(List.of(courseGrade));
        GradeListDto gradeListDto = gradeService.getStudentGrades(user.getUsername());

        assertEquals(1,gradeListDto.getGrades().size());
        assertEquals("student",gradeListDto.getGrades().get(0).getStudentUsername());
        assertEquals("course",gradeListDto.getGrades().get(0).getCourseName());
        assertEquals(5,gradeListDto.getGrades().get(0).getGrade());
    }

    @Test
    void shouldNotFindCourseGrade(){
        String teacherUsername = "teacher";
        mockAuthentication(teacherUsername);
        User teacher = User.builder().username("teacher").build();
        GradeAssignDto gradeAssignDto = new GradeAssignDto(user.getUsername(),course.getName(),9);
        course.setTeacher(teacher);

        when(userRepository.findTeacherByUsername(teacherUsername)).thenReturn(Optional.of(teacher));
        when(userRepository.findStudentByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(courseRepository.findByNameAndTeacher(course.getName(), teacher)).thenReturn(Optional.of(course));
        when(courseGradeRepository.findByStudentAndCourse(user, course)).thenReturn(Optional.empty());
        assertThrows(CourseGradeNotFoundException.class,()->gradeService.grade(gradeAssignDto));
    }
    @Test
    void shouldAssignGrade() {
        String teacherUsername = "teacher";
        mockAuthentication(teacherUsername);
        User teacher = User.builder().username("teacher").build();

        course.setTeacher(teacher);

        GradeAssignDto gradeAssignDto = new GradeAssignDto(user.getUsername(),course.getName(),9);

        when(userRepository.findTeacherByUsername(teacherUsername)).thenReturn(Optional.of(teacher));
        when(userRepository.findStudentByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(courseRepository.findByNameAndTeacher(course.getName(), teacher)).thenReturn(Optional.of(course));
        when(courseGradeRepository.findByStudentAndCourse(user, course)).thenReturn(Optional.of(courseGrade));

        GradeDto result = gradeService.grade(gradeAssignDto);

        assertEquals(9,courseGrade.getGrade());
    }

}