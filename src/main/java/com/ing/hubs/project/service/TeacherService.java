package com.ing.hubs.project.service;

import com.ing.hubs.project.dto.request.TeacherCreationDto;
import com.ing.hubs.project.dto.response.TeacherDto;
import com.ing.hubs.project.dto.response.TeacherListDto;
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
public class TeacherService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    public TeacherDto createTeacher(TeacherCreationDto teacherCreationDto){
        userRepository.findByUsername(teacherCreationDto.getUsername())
                .ifPresent(user -> { throw new UserAlreadyExistsException(); });
        var teacher = modelMapper.map(teacherCreationDto, User.class);
//        teacher.setPassword(passwordEncoder.encode(userCreationDto.getPassword()));
        teacher.setRole(Roles.TEACHER);
        var savedEntity = userRepository.save(teacher);

        return modelMapper.map(savedEntity, TeacherDto.class);
    }

    public User loadTeacher(String username) {
        return userRepository.findTeacherByUsername(username).orElseThrow(UserNotFoundException::new);
    }
    public TeacherDto getTeacher(String username) {
        var user = loadTeacher(username);
        return modelMapper.map(user, TeacherDto.class);
    }
    public TeacherListDto getAll() {
        return new TeacherListDto(userRepository.findAll().stream()
                .filter(user->user.getRole().equals(Roles.TEACHER))
                .map(user -> modelMapper.map(user, TeacherDto.class))
                .toList());
    }
    public void delete(String username){
        var teacher = loadTeacher(username);
        userRepository.delete(teacher);
    }

}
