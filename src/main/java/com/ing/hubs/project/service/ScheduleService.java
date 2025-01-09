package com.ing.hubs.project.service;

import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.Request;
import com.ing.hubs.project.entity.Schedule;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.RequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@AllArgsConstructor
@Service
public class ScheduleService {
    private RequestRepository requestRepository;
    private CourseRepository courseRepository;

    boolean hasConflicts(Schedule course1, Schedule course2){

        if(!course1.getWeekDay().equals(course2.getWeekDay()))
            return false;
        if (course1.getEndDate().isBefore(course2.getStartDate())
                || course1.getStartDate().isAfter(course2.getEndDate()))
            return false;
        return !course1.getEndTime().isBefore(course2.getStartTime())
                && !course1.getStartTime().isAfter(course2.getEndTime());
    }

    boolean validTime(String user_name, Course current_course) {
        List<Request> yourRequests = requestRepository.findUserRequests(user_name);
        List<Schedule> your_schedules = yourRequests.stream()
                .map(Request::getCourse)
                .flatMap(course -> course.getSchedule().stream())
                .toList();

        return current_course.getSchedule().stream()
                .noneMatch(currentSchedule -> your_schedules.stream()
                        .anyMatch(schedule -> hasConflicts(schedule, currentSchedule)));

    }
    boolean validCourseTime(User teacher, Course current_course) {
        List<Course> yourRequests = courseRepository.findByTeacher(teacher);

        List<Schedule> your_schedules = yourRequests.stream()
                .flatMap(course -> course.getSchedule().stream())
                .toList();

        return current_course.getSchedule().stream()
                .noneMatch(currentSchedule -> your_schedules.stream()
                        .anyMatch(schedule -> hasConflicts(schedule, currentSchedule)));

    }
}
