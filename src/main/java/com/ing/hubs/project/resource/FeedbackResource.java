package com.ing.hubs.project.resource;

import com.ing.hubs.project.dto.request.FeedbackCreationDto;
import com.ing.hubs.project.dto.response.FeedbackDto;
import com.ing.hubs.project.dto.response.FeedbackListDto;
import com.ing.hubs.project.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/feedback")
public class FeedbackResource {
    private FeedbackService feedbackService;
    private ModelMapper modelMapper;

    @PostMapping("/addFeedback/{course_name}")
    @Secured("ROLE_STUDENT")
    public FeedbackDto addFeedback(
            @PathVariable("course_name") String course_name,
            @RequestBody FeedbackCreationDto feedbackDto) {

        feedbackDto.setCourse_name(course_name);
        FeedbackDto createdFeedback = feedbackService.createFeedback(feedbackDto);

        return modelMapper.map(createdFeedback, FeedbackDto.class);
    }

    @GetMapping("/{name}")
    public FeedbackListDto getFeedbacksForCourse(@PathVariable String name) {
        return feedbackService.getAll(name);
    }


}
