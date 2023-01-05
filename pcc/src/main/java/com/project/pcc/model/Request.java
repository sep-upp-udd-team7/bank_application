package com.project.pcc.model;

import com.project.pcc.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acquirer_order_id")
    private String acquirerOrderId;

    @Column(name = "acquirer_timestamp")
    private LocalDateTime acquirerTimestamp;

    @Column(name = "cardholder_name")
    private String cardHolderName;

    @Column(name = "pan")
    private String pan;

    @Column(name = "mm")
    private String mm;

    @Column(name = "yy")
    private String yy;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "merchant_order_id")
    private String merchantOrderId;

    @Column(name = "merchant_timestamp")
    private LocalDateTime merchantTimestamp;

    @Column(name = "issuer_order_id")
    private String issuerOrderId;

    @Column(name = "issuer_timestamp")
    private LocalDateTime issuerTimestamp;

    @Column(name = "success_url")
    private String successURL;

    @Column(name = "failed_url")
    private String failedURL;

    @Column(name = "error_url")
    private String errorURL;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}
