package com.project.bank1.dto;

import lombok.Data;

@Data
public class AcquirerResponseDto {
    private String paymentUrl;
    private String paymentId;
    private String acquirer;
    private String acquirerBankAccount;
    private Double amount;
}
