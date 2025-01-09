package com.ing.hubs.project.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ing.hubs.project.dto.request.ScheduleCreationDto;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCreationDto {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Min(10)
    @Max(100)
    private Integer maxAttendees;
    private List<ScheduleCreationDto> schedule;
}
