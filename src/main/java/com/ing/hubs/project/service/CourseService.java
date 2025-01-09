package com.ing.hubs.project.service;
import com.ing.hubs.project.dto.request.CourseCreationDto;
import com.ing.hubs.project.dto.response.CourseDto;
import com.ing.hubs.project.dto.response.CourseListDto;
import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.exception.CourseNotFoundException;
import com.ing.hubs.project.exception.CourseOverlapingException;
import com.ing.hubs.project.exception.NoScheduleFoundException;
import com.ing.hubs.project.exception.CourseAlreadyExistsException;
import com.ing.hubs.project.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CourseService {

    private CourseRepository courseRepository;
    private TeacherService teacherService;
    private ModelMapper modelMapper;
    private ScheduleService scheduleService;


    public User getAuthenticatedTeacher(){
        String username =   ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return teacherService.loadTeacher(username);
    }

    public CourseListDto getMyCourses(){
        var teacher = getAuthenticatedTeacher();

        return new CourseListDto(courseRepository.findByTeacher(teacher)
                .stream()
                .map(course->modelMapper.map(course,CourseDto.class))
                .toList()
        );
    }


    private void checkIfSameCourse(Course course){
        var teacher = getAuthenticatedTeacher();
        courseRepository.findByNameAndTeacher(course.getName(),teacher)
                .ifPresent(c->{throw new CourseAlreadyExistsException();});
    }

    private void addSchedule(Course course){
        if (course.getSchedule().isEmpty()){
            throw new NoScheduleFoundException();
        }
        course.getSchedule().forEach(schedule->schedule.setCourse(course));
    }
    public CourseDto createCourse(CourseCreationDto courseCreationDto){
        var teacher = getAuthenticatedTeacher();
        Course course = modelMapper.map(courseCreationDto, Course.class);
        checkIfSameCourse(course);
        course.setTeacher(teacher);
        course.setCurrentAttendees(0);
        addSchedule(course);

        if(!scheduleService.validCourseTime(teacher,course))
            throw new CourseOverlapingException();

        if(scheduleService.hasConflicts(course.getSchedule().get(0),course.getSchedule().get(1)))
            throw new CourseOverlapingException();


        var savedEntity = courseRepository.save(course);
        var courseModel = modelMapper.map(savedEntity,CourseDto.class);

        courseModel.setTeacherUsername(teacher.getUsername());
        return courseModel;
    }

    private Course loadCourse(String name, User teacher){
        return courseRepository.findByNameAndTeacher(name,teacher)
                .orElseThrow(CourseNotFoundException::new);
    }

    public CourseListDto getAll(){
        return new CourseListDto(courseRepository.findAll().stream()
                .map(course->modelMapper.map(course,CourseDto.class))
                .toList());
    }

    public CourseDto getCourse(String teacherName, String courseName){
        var teacher = teacherService.loadTeacher(teacherName);
        var course = loadCourse(courseName,teacher);
        var courseModel = modelMapper.map(course,CourseDto.class);
        courseModel.setTeacherUsername(teacher.getUsername());
        return courseModel;
    }

    public void delete(String name){
        var teacher = getAuthenticatedTeacher();
        var course = loadCourse(name,teacher);
        courseRepository.delete(course);
    }

}
