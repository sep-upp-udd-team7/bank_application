package com.project.bank1.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    private String merchantId;
    private String merchantPassword;
    private Double amount;
    private Long merchantOrderId;
    private LocalDateTime merchantTimestamp;
    private String successUrl;
    private String failedUrl;
    private String errorUrl;
}
