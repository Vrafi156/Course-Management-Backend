package com.ing.hubs.project.service;

import com.ing.hubs.project.entity.*;
import com.ing.hubs.project.exception.CourseNotFoundException;
import com.ing.hubs.project.exception.UserNotFoundException;
import com.ing.hubs.project.repository.CourseGradeRepository;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.RequestRepository;
import com.ing.hubs.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GenerationService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final RequestRepository requestRepository;
    private final CourseGradeRepository courseGradeRepository;
    private final PasswordEncoder passwordEncoder;

    private Integer populateWithStudents(){
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            users.add(User.builder()
                    .username("student" + i)
                    .email("student" + i + "@example.com")
                    .password(passwordEncoder.encode("password" + i))
                    .phoneNumber("12345678")
                    .role(Roles.STUDENT)
                    .build());
        }
        userRepository.saveAll(users);
        return users.size();
    }

    private Integer populateWithTeachers(){
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            users.add(User.builder()
                    .username("teacher" + i)
                    .email("teacher" + i + "@example.com")
                    .password(passwordEncoder.encode("password" + i))
                    .phoneNumber("98765432")
                    .role(Roles.TEACHER)
                    .build());
        }
        userRepository.saveAll(users);
        return users.size();
    }
    public Integer populateWithUsers(){
        return populateWithStudents()+populateWithTeachers();
    }

    List<Schedule> getJavaSchedule(){
        Schedule courseSchedule = Schedule.builder()
                .scheduleName(ScheduleName.COURSE)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .weekDay(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();
        Schedule labSchedule = Schedule.builder()
                .scheduleName(ScheduleName.LABORATORY)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .weekDay(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(13, 0))
                .build();
        return List.of(courseSchedule,labSchedule);
    }

    private List<Schedule> getOOPSchedule(){
        Schedule courseSchedule = Schedule.builder()
                .scheduleName(ScheduleName.COURSE)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .weekDay(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();
        Schedule seminarSchedule = Schedule.builder()
                .scheduleName(ScheduleName.SEMINAR)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .weekDay(DayOfWeek.THURSDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(13, 0))
                .build();
        return List.of(courseSchedule,seminarSchedule);
    }

    private List<Schedule> getWizardrySchedule(){
        Schedule courseSchedule = Schedule.builder()
                .scheduleName(ScheduleName.COURSE)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .weekDay(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();
        Schedule seminarSchedule = Schedule.builder()
                .scheduleName(ScheduleName.SEMINAR)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 1))
                .weekDay(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(13, 0))
                .build();
        return List.of(courseSchedule,seminarSchedule);
    }

    private Course buildPooCourse(User teacher){
        var pooCourse = Course.builder()
                .name("POO")
                .description("Programming Oriented Objects")
                .schedule(getOOPSchedule())
                .maxAttendees(20)
                .currentAttendees(0)
                .teacher(teacher)
                .build();
        pooCourse.getSchedule().forEach(schedule->schedule.setCourse(pooCourse));
        return pooCourse;
    }

    private Course buildJavaCourse(User teacher){
        var javaCourse = Course.builder()
                .name("Java")
                .description("Java Programming Language")
                .schedule(getJavaSchedule())
                .maxAttendees(20)
                .currentAttendees(0)
                .teacher(teacher)
                .build();
        javaCourse.getSchedule().forEach(schedule->schedule.setCourse(javaCourse));
        return javaCourse;
    }

    private Course buildWizardryCourse(User teacher){
        var wizardryCourse=Course.builder()
                .name("Wizardry")
                .description("You can either be a wizard or learn Java. They are practically the same anyway.")
                .schedule(getWizardrySchedule())
                .maxAttendees(20)
                .currentAttendees(0)
                .teacher(teacher)
                .build();
        wizardryCourse.getSchedule().forEach(schedule->schedule.setCourse(wizardryCourse));
        return wizardryCourse;
    }
    public Integer populateWithCourses(){
        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String teacherUsername = "teacher" + i;
            var teacher = userRepository.findTeacherByUsername(teacherUsername)
                    .orElseThrow(UserNotFoundException::new);
            var pooCourse = buildPooCourse(teacher);
            courses.add(pooCourse);
            var javaCourse = buildJavaCourse(teacher);
            courses.add(javaCourse);
            var wizardryCourse = buildWizardryCourse(teacher);
            courses.add(wizardryCourse);

        }
        courseRepository.saveAll(courses);
        return courses.size();
    }

    private CourseGrade buildCourseGrade(Course course, User student){
        return CourseGrade.builder()
                .course(course)
                .student(student)
                .grade(0)
                .build();
    }
    public List<Integer> addStudentsToCourses(){
        List<Request> requests = new ArrayList<>();
        List<CourseGrade> grades = new ArrayList<>();
        var teacher = userRepository.findTeacherByUsername("teacher1")
                .orElseThrow(UserNotFoundException::new);
        var course = courseRepository.findByNameAndTeacher("Wizardry",teacher)
                .orElseThrow(CourseNotFoundException::new);
        RequestStatus status;
        for (int i = 1; i <= 50; i++) {
            var student = userRepository.findByUsername("student" + i).orElseThrow();
            if (i<=19) {
                status = RequestStatus.ACCEPTED;
                grades.add(buildCourseGrade(course,student));
            }
            else {
                status = RequestStatus.PENDING;
            }
            Request request = Request.builder()
                    .student(student)
                    .course(course)
                    .timeSent(LocalDate.now())
                    .requestStatus(status)
                    .build();
            requests.add(request);
        }
        course.setCurrentAttendees(19);
        requestRepository.saveAll(requests);
        courseGradeRepository.saveAll(grades);
        return List.of(requests.size(),grades.size());
    }
}

