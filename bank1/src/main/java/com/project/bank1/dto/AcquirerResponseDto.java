package com.project.bank1.dto;

import lombok.Data;

@Data
public class AcquirerResponseDto {
    private RequestDto requestDto;
    private String paymentUrl;
    private Long paymentId;
}
