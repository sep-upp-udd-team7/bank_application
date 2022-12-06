package com.project.bank1.dto;

import lombok.Data;

@Data
public class AcquirerResponseDto {
    private String paymentUrl;
    private String paymentId;
}
