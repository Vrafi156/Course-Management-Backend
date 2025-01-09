package com.ing.hubs.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseGrade {

    @Id
    @GeneratedValue
    private Integer id;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "username")
    private User student;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JsonBackReference
    private Course course;

    private Integer grade;
}
