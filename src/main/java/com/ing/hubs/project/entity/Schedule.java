package com.ing.hubs.project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue
    private Integer id;
    @Enumerated(EnumType.STRING)
    private ScheduleName scheduleName;
    private LocalDate startDate;
    private LocalDate endDate;
    private DayOfWeek weekDay;
    private LocalTime startTime;
    private LocalTime endTime;

    @EqualsAndHashCode.Exclude
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
