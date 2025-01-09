package com.ing.hubs.project.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue
    private Integer id;

    private String message;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
