package com.ing.hubs.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ing.hubs.project.entity.ScheduleName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ScheduleCreationDto {
    @NotNull
    private ScheduleName name;
    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate startDate;
    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate endDate;
    @NotNull
    private DayOfWeek weekDay;
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
