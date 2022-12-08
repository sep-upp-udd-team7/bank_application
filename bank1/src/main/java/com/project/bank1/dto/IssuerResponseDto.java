package com.project.bank1.dto;

import java.time.LocalDateTime;

public class IssuerResponseDto {
    private String transactionStatus;
    private Long merchantOrderId;
    private Long acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private Long paymentId;
}
