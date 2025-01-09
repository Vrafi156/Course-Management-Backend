package com.ing.hubs.project.resource;

import com.ing.hubs.project.dto.request.FeedbackCreationDto;
import com.ing.hubs.project.dto.request.StudentCreationDto;
import com.ing.hubs.project.dto.response.FeedbackDto;
import com.ing.hubs.project.dto.response.StudentDto;
import com.ing.hubs.project.dto.response.StudentListDto;
import com.ing.hubs.project.service.FeedbackService;
import com.ing.hubs.project.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/students")
public class StudentResource {

    private StudentService studentService;
    private FeedbackService feedbackService;
    private ModelMapper modelMapper;

    @PostMapping
    @Secured("ROLE_ADMIN")
    public StudentDto createStudent(@RequestBody @Valid StudentCreationDto studentCreationDto) {
        return studentService.createStudent(studentCreationDto);
    }
    @Secured({"ROLE_STUDENT","ROLE_ADMIN","ROLE_TEACHER"})
    @GetMapping(path = "/show")
    public StudentListDto getAllStudents(){return studentService.getAll();}

    @GetMapping(path = "/{username}")
    @Secured({"ROLE_STUDENT","ROLE_ADMIN","ROLE_TEACHER"})
    public StudentDto getStudent(@PathVariable String username) {return studentService.getStudent(username);}

//    @PatchMapping(path = "/update/{username}")
//    public TeacherUsernameDto updateUsername(){}

    @Secured({"ROLE_STUDENT","ROLE_ADMIN"})
    @PostMapping("/{username}/addFeedback/{course_name}")
    public FeedbackDto addFeedback(
            @PathVariable("username") String username,
            @PathVariable("course_name") String course_name,
            @RequestBody FeedbackCreationDto feedbackDto) {

        feedbackDto.setCourse_name(course_name);

        FeedbackDto createdFeedback = feedbackService.createFeedback(feedbackDto);
        return modelMapper.map(createdFeedback, FeedbackDto.class);

    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(path = "/{username}")
    public void delete(@PathVariable String username) {
        studentService.delete(username);
    }
}
