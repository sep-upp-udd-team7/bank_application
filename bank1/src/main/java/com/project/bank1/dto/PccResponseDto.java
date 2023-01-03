package com.project.bank1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PccResponseDto {
    private String transactionStatus;
    private String issuerOrderId;
    private LocalDateTime issuerTimestamp;
    private String acquirerOrderId;
    private LocalDateTime acquirerTimestamp;
    private Long issuerBankAccountId;
}
