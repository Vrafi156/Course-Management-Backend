package com.ing.hubs.project.dto.response;

import com.ing.hubs.project.dto.request.RequestAdminCreationDto;
import com.ing.hubs.project.entity.RequestAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminListDto {
    private List<RequestAdminResponseDto> requests;
}
