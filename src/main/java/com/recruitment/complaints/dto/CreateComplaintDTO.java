package com.recruitment.complaints.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateComplaintDTO {

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Reporter is required")
    private String reporter;
}
