package com.ing.hubs.project.service;

import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.Request;
import com.ing.hubs.project.entity.Schedule;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private Schedule schedule1;
    private Schedule schedule2;
    private Schedule conflictingSchedule;
    private Course course1;
    private Course course2;
    private Course conflictingCourse;
    private Request request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        schedule1 = Schedule.builder()
                .weekDay(DayOfWeek.valueOf("MONDAY"))
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();

        schedule2 = Schedule.builder()
                .weekDay(DayOfWeek.valueOf("TUESDAY"))
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .build();


        conflictingSchedule = Schedule.builder()
                .weekDay(DayOfWeek.valueOf("MONDAY"))
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        course1 = Course.builder()
                .name("Devschool")
                .schedule(List.of(schedule1))
                .build();

        course2 = Course.builder()
                .name("Voodoo")
                .schedule(List.of(schedule2))
                .build();

        conflictingCourse = Course.builder()
                .name("Voodoo")
                .schedule(List.of(conflictingSchedule))
                .build();



        request = Request.builder()
                .student(User.builder().username("studentUser").build())
                .course(course1)
                .build();
    }

    @Test
    void shouldDetectNoConflictsBetweenSchedules() {
        boolean result = scheduleService.hasConflicts(schedule1, schedule2);

        assertFalse(result);
    }

    @Test
    void shouldDetectConflictsBetweenSchedules() {
        boolean result = scheduleService.hasConflicts(schedule1, conflictingSchedule);

        assertTrue(result);
    }

    @Test
    void shouldValidateTimeWithoutConflicts() {
        when(requestRepository.findUserRequests("studentUser")).thenReturn(List.of(request));

        boolean result = scheduleService.validTime("studentUser", course2);

        assertTrue(result);
        verify(requestRepository, times(1)).findUserRequests("studentUser");
    }

    @Test
    void shouldInvalidateTimeWithConflicts() {
        when(requestRepository.findUserRequests("studentUser")).thenReturn(List.of(request));

        boolean result = scheduleService.validTime("studentUser", conflictingCourse);

        assertFalse(result);
        verify(requestRepository, times(1)).findUserRequests("studentUser");
    }

    @Test
    void shouldValidateCourseTimeWithoutConflicts() {
        User teacher = User.builder().username("teacherUser").build();
        when(courseRepository.findByTeacher(teacher)).thenReturn(List.of(course1));

        boolean result = scheduleService.validCourseTime(teacher, course2);

        assertTrue(result);
        verify(courseRepository, times(1)).findByTeacher(teacher);
    }

    @Test
    void shouldInvalidateCourseTimeWithConflicts() {
        User teacher = User.builder().username("teacherUser").build();
        when(courseRepository.findByTeacher(teacher)).thenReturn(List.of(course1));

        boolean result = scheduleService.validCourseTime(teacher, conflictingCourse);

        assertFalse(result);
        verify(courseRepository, times(1)).findByTeacher(teacher);
    }
}
