package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.CourseCreationDto;
import com.ing.hubs.project.dto.request.ScheduleCreationDto;
import com.ing.hubs.project.dto.response.CourseDto;
import com.ing.hubs.project.dto.response.CourseListDto;
import com.ing.hubs.project.dto.response.ScheduleDto;
import com.ing.hubs.project.entity.*;
import com.ing.hubs.project.exception.CourseAlreadyExistsException;
import com.ing.hubs.project.exception.CourseNotFoundException;
import com.ing.hubs.project.exception.CourseOverlapingException;
import com.ing.hubs.project.exception.NoScheduleFoundException;
import com.ing.hubs.project.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private CourseService courseService;
    private User teacher;
    private Course course;
    private CourseCreationDto courseCreationDto;
    private CourseDto courseDto;
    private Schedule schedule1;
    private Schedule schedule2;
    private ScheduleDto scheduleDto;
    private ScheduleCreationDto scheduleCreationDto;

    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        teacher = User.builder()
                .username("user")
                .password("password")
                .build();
        schedule1 = Schedule.builder()
                .scheduleName(ScheduleName.COURSE)
                .build();
        schedule2 = Schedule.builder()
                .scheduleName(ScheduleName.LABORATORY)
                .build();

        course = Course.builder()
                .name("course")
                .maxAttendees(10)
                .build();
        scheduleCreationDto = ScheduleCreationDto.builder()
                .name(ScheduleName.COURSE)
                .build();
        courseCreationDto = CourseCreationDto.builder()
                .name("course")
                .schedule(List.of(scheduleCreationDto))
                .build();
        scheduleDto = ScheduleDto.builder()
                .scheduleName(ScheduleName.COURSE)
                .build();
        courseDto = new CourseDto(course.getName(),course.getMaxAttendees(),teacher.getUsername(),List.of(scheduleDto));
    }

    private void mockAuthentication(String username) {
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .role(Roles.TEACHER)
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldReturnCoursesForAuthenticatedTeacher(){
        mockAuthentication(teacher.getUsername());
        course.setTeacher(teacher);
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(courseRepository.findByTeacher(teacher)).thenReturn(List.of(course));
        when(modelMapper.map(course, CourseDto.class)).thenReturn(courseDto);

        CourseListDto courseListDto = courseService.getMyCourses();
        assertEquals(1,courseListDto.getCourses().size());
        assertEquals("course",courseListDto.getCourses().get(0).getName());
        assertEquals("user",courseListDto.getCourses().get(0).getTeacherUsername());
        assertEquals(10,courseListDto.getCourses().get(0).getMaxAttendees());
    }
    @Test
    void shouldReturnAllCourses(){

        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(modelMapper.map(course, CourseDto.class)).thenReturn(courseDto);
        CourseListDto courseListDto = courseService.getAll();
        assertEquals(1,courseListDto.getCourses().size());
        assertEquals("course",courseListDto.getCourses().get(0).getName());
        assertEquals(10,courseListDto.getCourses().get(0).getMaxAttendees());
    }

    @Test
    void shouldThrowCourseNotFound(){
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,()->courseService.getCourse(teacher.getUsername(),course.getName()));
    }
    @Test
    void shouldReturnCourse(){
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.ofNullable(course));
        when(modelMapper.map(course,CourseDto.class)).thenReturn(courseDto);
        CourseDto dto=courseService.getCourse(teacher.getUsername(),course.getName());
        assertEquals("course",dto.getName());
        assertEquals("user",dto.getTeacherUsername());
        assertEquals(10,dto.getMaxAttendees());
    }

    @Test
    void shouldThrowCourseAlreadyExists(){
        mockAuthentication(teacher.getUsername());
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(modelMapper.map(courseCreationDto,Course.class)).thenReturn(course);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.of(course));
        assertThrows(CourseAlreadyExistsException.class,()->courseService.createCourse(courseCreationDto));
    }

    @Test
    void shouldThrowNoScheduleFound(){
        mockAuthentication(teacher.getUsername());
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(modelMapper.map(courseCreationDto,Course.class)).thenReturn(course);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.empty());
        course.setSchedule(Collections.emptyList());
        assertThrows(NoScheduleFoundException.class,()->courseService.createCourse(courseCreationDto));
    }

    @Test
    void shouldThrowCourseOverlappingException(){
        mockAuthentication(teacher.getUsername());
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(modelMapper.map(courseCreationDto,Course.class)).thenReturn(course);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.empty());
        course.setSchedule(List.of(schedule1));
        when(scheduleService.validCourseTime(teacher,course)).thenReturn(false);
        assertThrows(CourseOverlapingException.class,()->courseService.createCourse(courseCreationDto));
    }

    @Test
    void shouldCreateCourse(){
        mockAuthentication(teacher.getUsername());
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(modelMapper.map(courseCreationDto,Course.class)).thenReturn(course);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.empty());
        course.setSchedule(List.of(schedule1,schedule2));
        when(courseRepository.save(course)).thenReturn(course);
        when(modelMapper.map(course,CourseDto.class)).thenReturn(courseDto);
        when(scheduleService.validCourseTime(teacher,course)).thenReturn(true);
        when(scheduleService.hasConflicts(course.getSchedule().get(0),course.getSchedule().get(1))).thenReturn(false);

        CourseDto dto = courseService.createCourse(courseCreationDto);

        assertEquals("course", schedule1.getCourse().getName());
        assertEquals(0,course.getCurrentAttendees());
        assertEquals("course",dto.getName());
        assertEquals("user",dto.getTeacherUsername());
        assertEquals(ScheduleName.COURSE,dto.getSchedule().get(0).getScheduleName());
        assertEquals(10,dto.getMaxAttendees());
    }

    @Test
    void shouldNotPermitDeletion(){
        mockAuthentication(teacher.getUsername());
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class,()->courseService.delete(course.getName()));
    }

    @Test
    void shouldAllowDeletion(){
        mockAuthentication(teacher.getUsername());
        when(teacherService.loadTeacher(teacher.getUsername())).thenReturn(teacher);
        when(courseRepository.findByNameAndTeacher(course.getName(),teacher)).thenReturn(Optional.ofNullable(course));
        courseService.delete(course.getName());
        verify(courseRepository,times(1)).delete(course);
    }

}
