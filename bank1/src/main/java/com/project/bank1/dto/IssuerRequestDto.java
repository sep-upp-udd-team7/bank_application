package com.project.bank1.dto;

import lombok.Data;

@Data
public class IssuerRequestDto {
    private RequestDto requestDto;
    private String cardHolderName;
    private String pan;
    private String mm;
    private String yy;
    private String cvv;
    private String paymentId;
}
