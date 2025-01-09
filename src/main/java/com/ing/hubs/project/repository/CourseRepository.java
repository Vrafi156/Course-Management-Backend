package com.ing.hubs.project.repository;

import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course,Integer> {
    Optional<Course> findByNameAndTeacher(String name, User teacher);

    List<Course> findByTeacher(User teacher);

    Optional<Course> findByname(String name);


    //List<Course> findStudentsEnrolledInCourse
}
