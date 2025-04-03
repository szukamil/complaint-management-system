package com.recruitment.complaints.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponseDTO {

    private Long id;
    private String productId;
    private String content;
    private LocalDateTime createdAt;
    private String reporter;
    private String country;
    private Integer counter;
}
