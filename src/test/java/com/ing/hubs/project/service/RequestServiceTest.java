package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.response.GradeDto;
import com.ing.hubs.project.dto.response.RequestDto;
import com.ing.hubs.project.dto.response.RequestListDto;
import com.ing.hubs.project.entity.*;
import com.ing.hubs.project.exception.*;
import com.ing.hubs.project.repository.CourseGradeRepository;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.RequestRepository;
import com.ing.hubs.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseGradeRepository courseGradeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CourseService courseService;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private RequestService requestService;

    private User student;
    private User teacher;
    private Course course;
    private Request request;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        student = User.builder()
                .username("studentUser")
                .role(Roles.STUDENT)
                .build();

        teacher = User.builder()
                .username("teacherUser")
                .role(Roles.TEACHER)
                .build();

        course = Course.builder()
                .name("CourseName")
                .teacher(teacher)
                .maxAttendees(30)
                .currentAttendees(20)
                .build();

        request = Request.builder()
                .student(student)
                .course(course)
                .requestStatus(RequestStatus.PENDING)
                .timeSent(LocalDate.now())
                .build();

        requestDto = RequestDto.builder()
                .studentName("studentUser")
                .course_name("CourseName")
                .requestStatus(RequestStatus.PENDING)
                .timeSent(LocalDate.now())
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(teacher);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreateRequest() {
        when(userRepository.findByUsername("studentUser")).thenReturn(Optional.of(student));
        when(userRepository.findTeacherByUsername("teacherUser")).thenReturn(Optional.of(teacher));
        when(courseRepository.findByNameAndTeacher("CourseName", teacher)).thenReturn(Optional.of(course));
        when(scheduleService.validTime("studentUser", course)).thenReturn(true);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        var result = requestService.createRequest("studentUser", "CourseName", "teacherUser");

        assertNotNull(result);
        assertEquals("studentUser", result.getStudentName());
        assertEquals("CourseName", result.getCourse_name());
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void shouldThrowExceptionIfRequestAlreadyExists() {
        when(userRepository.findByUsername("studentUser")).thenReturn(Optional.of(student));
        when(userRepository.findTeacherByUsername("teacherUser")).thenReturn(Optional.of(teacher));
        when(courseRepository.findByNameAndTeacher("CourseName", teacher)).thenReturn(Optional.of(course));
        when(requestRepository.findRequest("studentUser", "CourseName")).thenReturn(Optional.of(request));

        assertThrows(RequestAlreadyAnsweredException.class, () ->
                requestService.createRequest("studentUser", "CourseName", "teacherUser"));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void shouldGetTeacherRequests() {
        when(requestRepository.findTeacherRequests("teacherUser")).thenReturn(List.of(request));
        when(modelMapper.map(request, RequestDto.class)).thenReturn(requestDto);

        var result = requestService.getTeachersRequest("teacherUser");

        assertNotNull(result);
        assertEquals(1, result.getRequests().size());
        assertEquals("studentUser", result.getRequests().get(0).getStudentName());
        verify(requestRepository, times(1)).findTeacherRequests("teacherUser");
    }

    @Test
    void shouldGetStudentRequests() {
        when(requestRepository.findUserRequests("studentUser")).thenReturn(List.of(request));
        when(modelMapper.map(request, RequestDto.class)).thenReturn(requestDto);

        var result = requestService.getStudentRequest("studentUser");

        assertNotNull(result);
        assertEquals(1, result.getRequests().size());
        assertEquals("CourseName", result.getRequests().get(0).getCourse_name());
        verify(requestRepository, times(1)).findUserRequests("studentUser");
    }

    @Test
    void shouldAcceptRequest() {
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(courseGradeRepository.save(any(CourseGrade.class))).thenReturn(new CourseGrade());

        requestService.acceptRequest(1);

        assertEquals(RequestStatus.ACCEPTED, request.getRequestStatus());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void shouldRejectRequest() {
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));

        requestService.rejectRequest(1);

        assertEquals(RequestStatus.REJECTED, request.getRequestStatus());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void shouldThrowExceptionIfRequestNotFound() {
        when(requestRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> requestService.acceptRequest(1));
        verify(requestRepository, never()).save(any(Request.class));
    }
}
