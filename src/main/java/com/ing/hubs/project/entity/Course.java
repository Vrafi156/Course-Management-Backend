package com.ing.hubs.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ing.hubs.project.exception.FullCourseException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private Integer maxAttendees;
    private Integer currentAttendees;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Schedule> schedule = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Request> requests = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "teacher_username")
    private User teacher;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany
    @JoinColumn(name = "course")
    private List<Feedback> feedbacks = new ArrayList<>();

    public void addAttendee(){
        if (currentAttendees>=maxAttendees){
            throw new FullCourseException();
        }
        this.currentAttendees=this.currentAttendees+1;
    }

}
