package com.ing.hubs.project.dto.response;

import com.ing.hubs.project.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestAdminResponseDto {
    private int id;
    private String fieldName;
    private String newValue;
    private RequestStatus requestStatus;
    private LocalDateTime timeSend;
    private String details;
    private String sender_username;
}
