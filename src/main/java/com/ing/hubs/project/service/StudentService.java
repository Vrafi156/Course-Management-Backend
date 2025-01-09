package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.StudentCreationDto;
import com.ing.hubs.project.dto.response.StudentDto;
import com.ing.hubs.project.dto.response.StudentListDto;
import com.ing.hubs.project.entity.Roles;
import com.ing.hubs.project.entity.User;
import com.ing.hubs.project.exception.UserAlreadyExistsException;
import com.ing.hubs.project.exception.UserNotFoundException;
import com.ing.hubs.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class StudentService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    public StudentDto createStudent(StudentCreationDto studentCreationDto){
        userRepository.findByUsername(studentCreationDto.getUsername())
                .ifPresent(user -> { throw new UserAlreadyExistsException(); });
        var student = modelMapper.map(studentCreationDto, User.class);
        //student.setPassword(passwordEncoder.encode(userCreationDto.getPassword()));
        student.setRole(Roles.STUDENT);
        var savedEntity = userRepository.save(student);

        return modelMapper.map(savedEntity, StudentDto.class);
    }

    public User loadStudent(String username) {
        return userRepository.findStudentByUsername(username).orElseThrow(UserNotFoundException::new);
    }
    public StudentDto getStudent(String username) {
        var user = loadStudent(username);
        return modelMapper.map(user, StudentDto.class);
    }
    public StudentListDto getAll() {
        return new StudentListDto(userRepository.findAll().stream()
                .filter(user->user.getRole().equals(Roles.STUDENT))
                .map(user -> modelMapper.map(user, StudentDto.class))
                .toList());
    }

    public void delete(String username){
        var student = loadStudent(username);
        userRepository.delete(student);
    }

}
