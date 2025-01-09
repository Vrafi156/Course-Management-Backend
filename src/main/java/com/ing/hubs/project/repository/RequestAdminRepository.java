package com.ing.hubs.project.repository;

import com.ing.hubs.project.entity.Feedback;
import com.ing.hubs.project.entity.RequestAdmin;
import com.ing.hubs.project.entity.RequestStatus;
import com.ing.hubs.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface RequestAdminRepository  extends JpaRepository<RequestAdmin, Integer> {

    void deleteById(Integer id);

    ArrayList<RequestAdmin> findByUser(User user);

    ArrayList<RequestAdmin> findAllByRequestStatus(RequestStatus requestStatus);
}
