package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.FeedbackCreationDto;
import com.ing.hubs.project.dto.response.FeedbackDto;
import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.Feedback;
import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.client.ExpectedCount.times;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {
    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private FeedbackService feedbackService;
    private Course course;
    private Feedback feedback;
    private FeedbackCreationDto feedbackCreationDto;
    private FeedbackDto feedbackDto;
    private User teacher;
    private User student;
    @BeforeEach
    void beforeEach() {
        student = User.builder()
                .username("user")
                .password("password")
                .build();

        course = Course.builder()
                .name("course")
                .build();

        feedback = Feedback.builder()
                .id(1)
                .message("feedback")
                .course(course)
                .build();

        feedbackCreationDto = FeedbackCreationDto.builder()
                .course_name("feedback")
                .message("feedback")
                .build();

        feedbackDto = new FeedbackDto(1, "Cursul tau e smecher");
    }

    private void mockAuthentification(String username) {
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .role(Roles.STUDENT)
                .build();
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldCreateFeedback() {
        mockAuthentification("user");

        when(courseRepository.findByname("feedback")).thenReturn(Optional.of(course));
        when(modelMapper.map(feedbackCreationDto, Feedback.class)).thenReturn(feedback);
        when(feedbackRepository.save(feedback)).thenReturn(feedback);
        when(modelMapper.map(feedback, FeedbackDto.class)).thenReturn(feedbackDto);

        FeedbackDto result = feedbackService.createFeedback(feedbackCreationDto);

        assertNotNull(result);
        assertEquals("Cursul tau e smecher", result.getMessage());
        assertEquals(1, result.getId());
    }


}


