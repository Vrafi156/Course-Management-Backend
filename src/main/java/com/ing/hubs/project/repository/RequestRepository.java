package com.ing.hubs.project.repository;

import com.ing.hubs.project.entity.Course;
import com.ing.hubs.project.entity.Feedback;
import com.ing.hubs.project.entity.Request;
import com.ing.hubs.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer>  {
//todo add check to see if there are the same requests.
    @Query("SELECT a FROM Request a WHERE a.course.teacher.username = :teacherUsername")
    List<Request> findTeacherRequests(@Param("teacherUsername") String teacherUsername);

    @Query("SELECT a FROM Request a WHERE a.course.name = :courseName")
    List<Request> findCourseRequests(@Param("courseName") String courseName);

    @Query("SELECT a FROM Request a WHERE a.student.username = :username")
    List<Request> findUserRequests(@Param("username") String username);


    @Query("""
    SELECT r
    FROM Request r
    JOIN r.course c
    WHERE r.student.username = :username AND c.name = :courseName
""")
    Optional<Request> findRequest(@Param("username") String username, @Param("courseName") String courseName);

}
