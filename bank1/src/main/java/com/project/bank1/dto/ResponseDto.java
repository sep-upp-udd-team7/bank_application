package com.project.bank1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponseDto {
    private String merchantOrderId;
    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private String paymentId;
    private String transactionStatus;
}
