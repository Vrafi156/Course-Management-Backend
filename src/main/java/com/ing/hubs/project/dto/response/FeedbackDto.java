package com.ing.hubs.project.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class FeedbackDto {
    private Integer id;
    private String message;

}
