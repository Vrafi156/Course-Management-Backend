package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.FeedbackCreationDto;
import com.ing.hubs.project.dto.response.FeedbackDto;
import com.ing.hubs.project.dto.response.FeedbackListDto;
import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.Feedback;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.FeedbackRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FeedbackService {
    private FeedbackRepository feedbackRepository;
    private ModelMapper modelMapper;
    private CourseRepository courseRepository;

    public FeedbackDto createFeedback(FeedbackCreationDto feedbackCreationDto){

        Course course = courseRepository.findByname(feedbackCreationDto.getCourse_name())
                .orElseThrow(() -> new IllegalArgumentException("Course doesn't exist"));
        var feedback = modelMapper.map(feedbackCreationDto, Feedback.class);
        feedback.setMessage(feedbackCreationDto.getMessage());
        feedback.setCourse(course);

        var savedEntity = feedbackRepository.save(feedback);
        return modelMapper.map(savedEntity, FeedbackDto.class);
    }

    private Feedback loadFeedback(Integer id) { return feedbackRepository.findById(id).orElseThrow();}

    public FeedbackListDto getAll(String Course_name) {

        Course course = courseRepository.findByname(Course_name)
                .orElseThrow(() -> new IllegalArgumentException("Course doesn't exist"));


        return new FeedbackListDto(feedbackRepository.findCourseFeedbacks(Course_name).stream()
                .map(user -> modelMapper.map(user, FeedbackDto.class))
                .toList());
    }
    public void delete(Integer id){
        var feedback = loadFeedback(id);
        feedbackRepository.delete(feedback);
    }

}
