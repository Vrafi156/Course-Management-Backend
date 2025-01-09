package com.ing.hubs.project.dto.request;

import com.github.fge.jsonpatch.JsonPatch;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GradeAssignDto {
    @NotBlank
    private String username;
    @NotBlank
    private String courseName;
    @Min(1)
    @Max(10)
    private Integer grade;
}
