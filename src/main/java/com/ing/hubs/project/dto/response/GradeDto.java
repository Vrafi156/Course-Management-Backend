package com.ing.hubs.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {
    private String studentUsername;
    private String courseName;
    //Maybe?
    private String teacherUsername;
    private Integer grade;
}
