package com.project.bank1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bank1.model.Transaction;
import lombok.Data;

import javax.persistence.Column;
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
    private Double amount;
    private String merchantOrderId;
    private LocalDateTime merchantTimestamp;
    private String successURL;
    private String failedURL;
    private String errorURL;
    private Boolean qrCodePayment = false;
    private String bankName;
    private String issuer;

}
