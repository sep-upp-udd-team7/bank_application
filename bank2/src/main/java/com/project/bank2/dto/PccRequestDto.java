package com.project.bank2.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PccRequestDto {
    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private String cardHolderName;
    private String pan;
    private String mm;
    private String yy;
    private String cvv;
    private String paymentId;
}
