package com.ing.hubs.project.repository;

import com.ing.hubs.project.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository  extends JpaRepository<Feedback, Integer> {
    @Query("SELECT a FROM Feedback a WHERE a.course.name = :name")
    List<Feedback> findCourseFeedbacks(String name);
}
