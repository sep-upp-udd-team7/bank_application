package com.project.bank1.dto;

import lombok.Data;

@Data
public class IssuerRequestDto {
    private String cardHolderName;
    private String pan;
    private String mm;
    private String yy;
    private String cvv;
    private String paymentId;
    private String issuer;
    private String bankName;
}
