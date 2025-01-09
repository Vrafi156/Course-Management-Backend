package com.ing.hubs.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user", indexes = {@Index(name = "idx_role", columnList = "role")})

public class User implements UserDetails{
    @Id //both Teachers and Students should have the same basic fields
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private Roles role;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Request> requests = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<CourseGrade> courseGrades = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () ->"ROLE_"+ role.name());
    }


}
