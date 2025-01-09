package com.ing.hubs.project.resource;

import com.ing.hubs.project.dto.request.RequestAdminCreationDto;
import com.ing.hubs.project.dto.response.AdminListDto;
import com.ing.hubs.project.dto.response.RequestAdminResponseDto;
import com.ing.hubs.project.entity.RequestStatus;
import com.ing.hubs.project.service.RequestAdminService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class RequestAdminResource {
    private RequestAdminService requestAdminService;

    @Secured({"ROLE_TEACHER", "ROLE_STUDENT","ROLE_ADMIN"})
    @PatchMapping
    public RequestAdminCreationDto createUpdateRequest(@RequestBody RequestAdminCreationDto requestAdminCreationDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user_name =  authentication.getName();
        return requestAdminService.createRequest(requestAdminCreationDto, user_name);
    }

    @Secured("ROLE_ADMIN")
    @PatchMapping("/respond/{requestId}")
    public RequestAdminResponseDto getConfirmation(@PathVariable Integer requestId,
                                                   @RequestBody RequestAdminResponseDto adminResponseDto) {

      return requestAdminService.respondToRequest(requestId, adminResponseDto);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/show")
    public AdminListDto getConfirmation() {
        return requestAdminService.getAllRequests();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/delete/{id}")
    public void getConfirmation(@PathVariable Integer id) {
         requestAdminService.delete(id);
    }


    @Secured({"ROLE_TEACHER", "ROLE_STUDENT","ROLE_ADMIN"})
        @GetMapping("/user")
        public AdminListDto getUserRequests() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var user_name =  authentication.getName();
            return  requestAdminService.getUserRequest(user_name);
        }

    @Secured({"ROLE_STUDENT","ROLE_TEACHER"})
    @PatchMapping("/accept/{id}")
    public RequestAdminResponseDto acceptRequest(@PathVariable Integer id){
        return requestAdminService.updatePatchStatus(id, RequestStatus.ACCEPTED);
    }

    @Secured({"ROLE_STUDENT","ROLE_TEACHER"})
    @PatchMapping("/refuse/{id}")
    public RequestAdminResponseDto reject(@PathVariable Integer id){
        return requestAdminService.updatePatchStatus(id, RequestStatus.REJECTED);
    }


    @Secured("ROLE_ADMIN")
    @PatchMapping("/process")
    public void processAcceptedRequests(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user_name =  authentication.getName();
        requestAdminService.applyPatch(user_name);
    }

}
