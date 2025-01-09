package com.ing.hubs.project.resource;


import com.ing.hubs.project.dto.request.RequestCreationDto;
import com.ing.hubs.project.dto.response.GradeDto;
import com.ing.hubs.project.dto.response.RequestDto;
import com.ing.hubs.project.dto.response.RequestListDto;
import com.ing.hubs.project.repository.RequestRepository;
import com.ing.hubs.project.service.RequestService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;

@RestController
@AllArgsConstructor
@RequestMapping("/request")
public class RequestResource {
    public ModelMapper modelMapper;
    public RequestService requestService;

    @PostMapping("/{course}/{professor}")
    @Secured({"ROLE_STUDENT","ROLE_ADMIN"})
    public RequestDto createEnrollmentRequest(
            @PathVariable String course,
            @PathVariable String professor) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        var user_name =  authentication.getName();
        return requestService.createRequest(user_name,course,professor);

    }

    @GetMapping("/show")
    @Secured({"ROLE_TEACHER","ROLE_ADMIN"})
    public RequestListDto getMyTeacherRequests() {return requestService.getMyRequestsTeacher();}

    @GetMapping("/sent")
    @Secured({"ROLE_STUDENT","ROLE_ADMIN"})
    public RequestListDto getMyStudentRequests() {return requestService.getMyRequestsStudent();}

    //teacher should use course id to accept imo
    @PostMapping("/accept/{id}")
    @Secured({"ROLE_TEACHER","ROLE_ADMIN"})
    public GradeDto acceptRequest(@PathVariable Integer id) {return requestService.acceptRequest(id);}

    @PatchMapping("/reject/{id}")
    @Secured({"ROLE_TEACHER","ROLE_ADMIN"})
    public void rejectRequest(@PathVariable Integer id) {requestService.rejectRequest(id);}

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN","ROLE_TEACHER"})
    public void deleteRequest(@PathVariable Integer id) {requestService.deleteRequest(id);}

}
