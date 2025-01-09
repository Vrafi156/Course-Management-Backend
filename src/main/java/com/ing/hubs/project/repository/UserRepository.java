package com.ing.hubs.project.repository;

import com.ing.hubs.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM user WHERE username = ?1 AND role = 'TEACHER'",nativeQuery = true)
    Optional<User> findTeacherByUsername(String id);

    @Query(value = "SELECT * FROM user WHERE username = ?1 AND role = 'STUDENT'",nativeQuery = true)
    Optional<User> findStudentByUsername(String id);
}
