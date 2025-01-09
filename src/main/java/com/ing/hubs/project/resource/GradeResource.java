package com.ing.hubs.project.resource;

import com.ing.hubs.project.dto.request.GradeAssignDto;
import com.ing.hubs.project.dto.response.GradeDto;
import com.ing.hubs.project.dto.response.GradeListDto;
import com.ing.hubs.project.service.GradeService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/grade")
public class GradeResource {

    private GradeService gradeService;
    @Secured("ROLE_TEACHER")
    @PatchMapping
    public GradeDto gradeStudent(@RequestBody GradeAssignDto gradeAssignDto){return gradeService.grade(gradeAssignDto);}

    @GetMapping("/{courseName}")
    @Secured("ROLE_TEACHER")
    public GradeListDto getStudentsFromCourse(@PathVariable String courseName) {return gradeService.getEnrolledStudents(courseName);}
    @GetMapping("/all")
    @Secured("ROLE_STUDENT")
    public GradeListDto getMyGrades() {return gradeService.getMyGrades();}


}
