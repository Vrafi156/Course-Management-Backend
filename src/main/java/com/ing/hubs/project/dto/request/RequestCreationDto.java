package com.ing.hubs.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ing.hubs.project.entity.RequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RequestCreationDto {

//    @NotNull
//    @JsonFormat(pattern = "dd.MM.yyyy")
//    private LocalDate timeSent;

}
