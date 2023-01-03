package com.project.bank2.dto;

import lombok.Data;

@Data
public class AcquirerResponseDto {
    private String paymentUrl;
    private String paymentId;
}
