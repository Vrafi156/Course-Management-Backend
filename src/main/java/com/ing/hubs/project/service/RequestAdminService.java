package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.RequestAdminCreationDto;
import com.ing.hubs.project.dto.response.*;
import com.ing.hubs.project.entity.*;
import com.ing.hubs.project.exception.RequestAlreadyAnsweredException;
import com.ing.hubs.project.exception.RequestNotFoundException;
import com.ing.hubs.project.exception.UnsuportedFieldException;
import com.ing.hubs.project.repository.RequestAdminRepository;
import com.ing.hubs.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service

public class RequestAdminService {
    private RequestAdminRepository requestAdminRepository;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    public RequestAdminCreationDto createRequest(RequestAdminCreationDto requestAdminDto,
                                                 String user_name) {
        User sender = userRepository.findByUsername(user_name)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!checkField(requestAdminDto.getField()))
            throw new UnsuportedFieldException("Unsupported field: " + requestAdminDto.getField());


        RequestAdmin requestAdmin = RequestAdmin.builder()
                .field(requestAdminDto.getField())
                .value(requestAdminDto.getValue())
                .timeSend(LocalDateTime.now())
                .requestStatus(RequestStatus.PENDING)
                .user(sender)
                .build();
        requestAdminRepository.save(requestAdmin);


        var response =  modelMapper.map(requestAdminRepository, RequestAdminCreationDto.class);
        response.setField(requestAdmin.getField());
        response.setValue(requestAdmin.getValue());

        return response;
    }


    @Secured("ROLE_ADMIN")
    public AdminListDto getAllRequests(){
        return new AdminListDto(requestAdminRepository.findAll().stream()
                .map(request -> {
                    User sender = request.getUser();
                    return RequestAdminResponseDto.builder()
                            .id(request.getId())
                            .fieldName(request.getField())
                            .newValue(request.getValue())
                            .requestStatus(request.getRequestStatus())
                            .timeSend(request.getTimeSend())
                            .sender_username(sender.getUsername())
                            .details("Confirmi aceasta cerere pentru " + request.getField() + ": " + request.getValue())
                            .build();
                })
                .collect(Collectors.toList()));
    }

    @Secured("ROLE_ADMIN")
    public RequestAdminResponseDto respondToRequest(Integer requestId, RequestAdminResponseDto adminResponseDto) {
        RequestAdmin requestAdmin = requestAdminRepository.findById(requestId)
                .orElseThrow(RequestNotFoundException::new);

        if (requestAdmin.getRequestStatus() != RequestStatus.PENDING) {
            throw new RequestAlreadyAnsweredException();
        }
        requestAdmin.setRequestStatus(RequestStatus.CONFIRMATION);
        requestAdmin.setTimeSend(LocalDateTime.now());
        requestAdminRepository.save(requestAdmin);

        return getResponse(requestAdmin);

    }

    public void delete(int id) {
        requestAdminRepository.deleteById(id);

    }

    public AdminListDto getUserRequest(String user_name) {
        User user = userRepository.findByUsername(user_name)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ArrayList<RequestAdmin> requests = requestAdminRepository.findByUser(user);

        return new AdminListDto(requests.stream()
                .map(request -> RequestAdminResponseDto.builder()
                        .id(request.getId())
                        .fieldName(request.getField())
                        .newValue(request.getValue())
                        .requestStatus(request.getRequestStatus())
                        .timeSend(request.getTimeSend())
                        .sender_username(request.getUser().getUsername())
                        .details("Confirmi aceasta cerere pentru " + request.getField() + ": " + request.getValue())
                        .build())
                .collect(Collectors.toList()));
    }

    private void checkIfRequestBelongsToUser(RequestAdmin request){
        String username =   ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        if(!request.getUser().getUsername().equals(username)){
            throw new RequestNotFoundException();
        }
    }

    private RequestAdmin loadRequest(Integer id){
        return requestAdminRepository.findById(id).orElseThrow(RequestNotFoundException::new);
    }

    public RequestAdminResponseDto updatePatchStatus(Integer id, RequestStatus status) {
        var request = loadRequest(id);
        checkIfRequestBelongsToUser(request);
        if (request.getRequestStatus()!=RequestStatus.CONFIRMATION){
            throw new RequestAlreadyAnsweredException();
        }
        request.setRequestStatus(status);
        requestAdminRepository.save(request);

        return getResponse(request);
    }

    public void applyPatch(String user_name) {

        ArrayList<RequestAdmin> acceptedRequests = requestAdminRepository.findAllByRequestStatus(RequestStatus.ACCEPTED);

        acceptedRequests.forEach(request -> {
            User user = request.getUser();
            switch (request.getField()) {
                case "email" -> user.setEmail(request.getValue());
                case "username" -> user.setUsername(request.getValue());
                case "password" -> user.setPassword(passwordEncoder.encode(user.getPassword()));
                default -> throw new UnsuportedFieldException("Unsupported field: " + request.getField());
            }
            userRepository.save(user);
            request.setRequestStatus(RequestStatus.SOLVED);
            requestAdminRepository.save(request);
        });
    }

    private RequestAdminResponseDto  getResponse(RequestAdmin request) {
        var response = modelMapper.map(request, RequestAdminResponseDto.class);
        response.setFieldName(request.getField());
        response.setNewValue(request.getValue());
        response.setSender_username(request.getUser().getUsername());
        response.setDetails("Confirmi aceasta cerere pentru " + request.getField() + ":" + request.getValue());
        return response;
    }
    private boolean checkField(String field) {
        if (field.equals("email") || field.equals("username")  || field.equals("password"))
            return true;
        return false;
    }

}


