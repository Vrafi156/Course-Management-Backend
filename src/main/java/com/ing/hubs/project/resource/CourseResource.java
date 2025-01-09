package com.ing.hubs.project.resource;

import com.ing.hubs.project.dto.request.CourseCreationDto;
import com.ing.hubs.project.dto.response.*;
import com.ing.hubs.project.service.CourseService;
import com.ing.hubs.project.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/courses")
public class CourseResource {

    private CourseService courseService;
    private FeedbackService feedbackService;

    @PostMapping
    @Secured("ROLE_TEACHER")
    public CourseDto createCourse(@RequestBody @Valid CourseCreationDto courseCreationDto) {
        return courseService.createCourse(courseCreationDto);
    }
    @GetMapping(path = "/show")
    @Secured({"ROLE_STUDENT","ROLE_TEACHER","ROLE_ADMIN"})
    public CourseListDto getAllCourses(){return courseService.getAll();}

    @GetMapping(path = "/me")
    @Secured("ROLE_TEACHER")
    public CourseListDto getMyCourses(){return courseService.getMyCourses();}

    @DeleteMapping(path="/{name}")
    @Secured({"ROLE_TEACHER","ROLE_ADMIN"})
    public void delete(@PathVariable String name) {courseService.delete(name);}

    @GetMapping(path="/{teacherName}/{courseName}")
    @Secured({"ROLE_TEACHER","ROLE_STUDENT","ROLE_ADMIN"})
    public CourseDto getCourse(@PathVariable String teacherName, @PathVariable String courseName) {
        return courseService.getCourse(teacherName,courseName);
    }


}
