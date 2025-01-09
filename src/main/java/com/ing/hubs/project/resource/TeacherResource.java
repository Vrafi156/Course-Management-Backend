package com.ing.hubs.project.resource;

import com.ing.hubs.project.dto.request.TeacherCreationDto;
import com.ing.hubs.project.dto.response.TeacherDto;
import com.ing.hubs.project.dto.response.TeacherListDto;
import com.ing.hubs.project.service.TeacherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/teachers")
public class TeacherResource {

    private TeacherService teacherService;

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public TeacherDto createTeacher(@RequestBody @Valid TeacherCreationDto teacher) {
        return teacherService.createTeacher(teacher);
    }

    @Secured({"ROLE_ADMIN","ROLE_TEACHER","ROLE_STUDENT"})
    @GetMapping(path = "/show")
    public TeacherListDto getAllTeachers(){return teacherService.getAll();}

    @Secured({"ROLE_STUDENT","ROLE_ADMIN","ROLE_TEACHER"})
    @GetMapping(path = "/{username}")
    public TeacherDto getTeacher(@PathVariable String username) {return teacherService.getTeacher(username);}

//    @PatchMapping(path = "/update/{username}")
//    public TeacherUsernameDto updateUsername(){}

    @Secured("ROLE_ADMIN")
    @DeleteMapping(path = "/{username}")
    public void delete(@PathVariable String username) {
        teacherService.delete(username);
    }
}
