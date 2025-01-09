package com.ing.hubs.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDate timeSent;
    @Enumerated(EnumType.STRING)
    RequestStatus requestStatus;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name = "student_id")
    private User student;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "course_id")
    private Course course;
}
