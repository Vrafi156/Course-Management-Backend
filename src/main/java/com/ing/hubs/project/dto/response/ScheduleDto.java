package com.ing.hubs.project.dto.response;

import com.ing.hubs.project.entity.ScheduleName;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDto {
    private ScheduleName scheduleName;

    private LocalDate startDate;

    private LocalDate endDate;

    private DayOfWeek weekDay;

    private LocalTime startTime;

    private LocalTime endTime;
}
