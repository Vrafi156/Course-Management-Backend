package com.ing.hubs.project.service;


import com.ing.hubs.project.dto.request.GradeAssignDto;
import com.ing.hubs.project.dto.response.GradeDto;
import com.ing.hubs.project.dto.response.GradeListDto;
import com.ing.hubs.project.entity.CourseGrade;
import com.ing.hubs.project.exception.CourseNotFoundException;
import com.ing.hubs.project.exception.CourseGradeNotFoundException;
import com.ing.hubs.project.exception.UserNotFoundException;
import com.ing.hubs.project.repository.CourseGradeRepository;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GradeService {

    private CourseGradeRepository courseGradeRepository;
    private UserRepository userRepository;
    private CourseRepository courseRepository;

    private GradeDto constructGradeDto(CourseGrade courseGrade){
        return GradeDto.builder()
                .courseName(courseGrade.getCourse().getName())
                .studentUsername(courseGrade.getStudent().getUsername())
                .teacherUsername(getAuthenticatedUserUsername())
                .grade(courseGrade.getGrade())
                .build();
    }

    private String getAuthenticatedUserUsername(){
        return((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    public GradeListDto getEnrolledStudents(String courseName){
        var teacher = userRepository.findTeacherByUsername(getAuthenticatedUserUsername())
                .orElseThrow(UserNotFoundException::new);
        var course = courseRepository.findByNameAndTeacher(courseName,teacher)
                .orElseThrow(CourseNotFoundException::new);
        return new GradeListDto(courseGradeRepository.findAll().stream()
                .filter(grade->grade.getCourse().equals(course))
                .map(this::constructGradeDto)
                .toList());
    }
    public GradeListDto getMyGrades(){
        String username = getAuthenticatedUserUsername();
        return new GradeListDto(courseGradeRepository.findAll().stream()
                .filter(grade->grade.getStudent().getUsername().equals(username))
                .map(this::constructGradeDto)
                .toList());
    }

    public GradeListDto getStudentGrades(String username){
        return new GradeListDto(courseGradeRepository.findAll().stream()
                .filter(grade->grade.getStudent().getUsername().equals(username))
                .map(this::constructGradeDto)
                .toList());
    }
    public GradeDto grade(GradeAssignDto gradeAssignDto){
        String username =  getAuthenticatedUserUsername();
        var student = userRepository.findStudentByUsername(gradeAssignDto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        var teacher = userRepository.findTeacherByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        var course = courseRepository.findByNameAndTeacher(gradeAssignDto.getCourseName(), teacher)
                .orElseThrow(CourseNotFoundException::new);

        var courseGrade = courseGradeRepository.findByStudentAndCourse(student,course)
                .orElseThrow(CourseGradeNotFoundException::new);

        courseGrade.setGrade(gradeAssignDto.getGrade());
        courseGradeRepository.save(courseGrade);
        return constructGradeDto(courseGrade);
    }
}
