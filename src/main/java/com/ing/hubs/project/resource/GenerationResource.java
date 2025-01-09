package com.ing.hubs.project.resource;

import com.ing.hubs.project.service.GenerationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/generation")
public class GenerationResource {

    private final GenerationService generationService;
    @PostMapping("/users")
    public void generateUsers(){generationService.populateWithUsers();}

    @PostMapping("/courses")
    public void generateCourses(){generationService.populateWithCourses();}

    @PostMapping("/links")
    public void addStudentsToCourses(){generationService.addStudentsToCourses();}
}
