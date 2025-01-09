package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.response.GradeDto;
import com.ing.hubs.project.dto.response.RequestDto;
import com.ing.hubs.project.dto.response.RequestListDto;
import com.ing.hubs.project.entity.*;
import com.ing.hubs.project.exception.*;
import com.ing.hubs.project.repository.CourseGradeRepository;
import com.ing.hubs.project.repository.CourseRepository;
import com.ing.hubs.project.repository.RequestRepository;
import com.ing.hubs.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.ing.hubs.project.entity.RequestStatus.PENDING;

@AllArgsConstructor
@Service
public class RequestService {
    private RequestRepository requestRepository;
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private CourseGradeRepository courseGradeRepository;
    private ModelMapper modelMapper;
    private CourseService courseService;
    private ScheduleService scheduleService;


    public RequestDto createRequest(String user_name, String course_name, String teacher) {
        User user = getUser(user_name);
        var course_teacher = getTeacher(teacher);
        Course current_course = getCourse(course_name, course_teacher);

        checkCourseCapacity(current_course);
        checkRequestDoesNotExist(user.getUsername(), current_course.getName());
        validateSchedule(user_name, current_course);
        LocalDate currentDate = LocalDate.now();
        var request = Request.builder()
                .student(user)
                .requestStatus(PENDING)
                .course(current_course)
                .timeSent(currentDate)
                .build();

        requestRepository.save(request);

        return sendRequestDTO(request);
    }

    public RequestListDto getTeachersRequest(String teacher) {
        var requests = requestRepository.findTeacherRequests(teacher);
        var requestDtos = requests.stream().map(request -> {
            RequestDto requestResponse = modelMapper.map(request, RequestDto.class);
            requestResponse.setStudentName(request.getStudent().getUsername());
            requestResponse.setCourse_name(request.getCourse().getName());
            return requestResponse;
        }).toList();

        return new RequestListDto(requestDtos);
    }

    public RequestListDto getCourseRequest(String course_name) {
        var requests = requestRepository.findCourseRequests(course_name);

        var requestDtos = requests.stream().map(request -> {
            RequestDto requestResponse = modelMapper.map(request, RequestDto.class);
            requestResponse.setStudentName(request.getStudent().getUsername());
            requestResponse.setCourse_name(request.getCourse().getName());
            return requestResponse;
        }).toList();

        return new RequestListDto(requestDtos);
    }

    public RequestListDto getStudentRequest(String user_name) {
        var requests = requestRepository.findUserRequests(user_name);
        var requestDtos = requests.stream().map(request -> {
            RequestDto requestResponse = modelMapper.map(request, RequestDto.class);
            requestResponse.setStudentName(request.getStudent().getUsername());
            requestResponse.setCourse_name(request.getCourse().getName());
            return requestResponse;
        }).toList();

        return new RequestListDto(requestDtos);
    }

    public void updateRequest(String user_name,
                              String course_name,
                              RequestStatus decision) {
        Request request = requestRepository.findRequest(user_name, course_name)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        System.out.println(request);
        request.setRequestStatus(decision);
        requestRepository.save(request);
    }


    private RequestDto buildRequestDto(Request request){
        return RequestDto.builder()
                .studentName(request.getStudent().getUsername())
                .course_name(request.getCourse().getName())
                .requestStatus(request.getRequestStatus())
                .timeSent(request.getTimeSent())
                .id(request.getId())
                .build();
    }
    public RequestListDto getMyRequestsTeacher(){
        var teacher = courseService.getAuthenticatedTeacher();

        return new RequestListDto(requestRepository.findAll()
                .stream()
                .filter(request->request.getCourse().getTeacher().getUsername().equals(teacher.getUsername()))
                .map(this::buildRequestDto)
                .toList()
        );
    }

    public RequestListDto getMyRequestsStudent(){
        String studentUsername =   ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return getStudentRequest(studentUsername);
    }

    private Request loadRequest(Integer id){
        return requestRepository.findById(id).orElseThrow(RequestNotFoundException::new);
    }

    private String getAuthenticatedUserUsername(){
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    private void checkIfRequestBelongsToUser(Request request){
        String username = getAuthenticatedUserUsername();
        if(!request.getCourse().getTeacher().getUsername().equals(username)){
            throw new RequestNotFoundException();
        }
    }

    private void checkIfRequestIsPending(Request request){
        if (request.getRequestStatus()!=RequestStatus.PENDING){
            throw new RequestAlreadyAnsweredException();
        }
    }
    public GradeDto acceptRequest(Integer id){
        String username = getAuthenticatedUserUsername();
        var request = loadRequest(id);
        checkIfRequestBelongsToUser(request);
        checkIfRequestIsPending(request);
        request.setRequestStatus(RequestStatus.ACCEPTED);

        var courseGrade = CourseGrade.builder()
                .student(request.getStudent())
                .course(request.getCourse())
                .grade(0)
                .build();

        request.getCourse().addAttendee();
        requestRepository.save(request);
        courseGradeRepository.save(courseGrade);

        return GradeDto.builder()
                .courseName(courseGrade.getCourse().getName())
                .studentUsername(courseGrade.getStudent().getUsername())
                .teacherUsername(username)
                .grade(courseGrade.getGrade())
                .build();
    }

    public void rejectRequest(Integer id){
        Request request = loadRequest(id);
        checkIfRequestBelongsToUser(request);
        checkIfRequestIsPending(request);
        request.setRequestStatus(RequestStatus.REJECTED);
        requestRepository.save(request);
    }

    public void deleteRequest(Integer id){
        var request = loadRequest(id);
        if (request.getRequestStatus().equals(RequestStatus.ACCEPTED)){
            throw new RequestNotFoundException();
        }
        requestRepository.delete(request);
    }


    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    private User getTeacher(String username) {
        return userRepository.findTeacherByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    private Course getCourse(String courseName, User teacher) {
        return courseRepository.findByNameAndTeacher(courseName, teacher)
                .orElseThrow(CourseNotFoundException::new);
    }


    private void checkCourseCapacity(Course course) {
        if (course.getMaxAttendees()<=course.getCurrentAttendees()){
            throw new FullCourseException();
        }
    }

    private void checkRequestDoesNotExist(String username, String courseName) {
        if (requestRepository.findRequest(username, courseName).isPresent()) {
            throw new RequestAlreadyAnsweredException();
        }
    }

    private void validateSchedule(String username, Course course) {
        if (!scheduleService.validTime(username, course)) {
            throw new CourseOverlapingException();
        }
    }

    private RequestDto sendRequestDTO(Request request) {
        return RequestDto.builder()
                .studentName(request.getStudent().getUsername())
                .course_name(request.getCourse().getName())
                .requestStatus(request.getRequestStatus())
                .timeSent(request.getTimeSent())
                .build();
    }



}
