package com.project.bank1.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssuerBankResponseDto {
    private String transactionStatus;
    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private String issuerOrderId;
    private LocalDateTime IssuerTimestamp;

}
