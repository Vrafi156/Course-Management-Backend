package com.ing.hubs.project.repository;

import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.CourseGrade;
import com.ing.hubs.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseGradeRepository extends JpaRepository<CourseGrade, Integer> {
    Optional<CourseGrade> findByStudentAndCourse(User student, Course course);

}
