package com.ing.hubs.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ing.hubs.project.dto.response.ScheduleDto;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private String name;
    private Integer maxAttendees;
    private String teacherUsername;
    private List<ScheduleDto> schedule;
}
