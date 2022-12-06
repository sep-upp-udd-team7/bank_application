package com.project.bank1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.bank1.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="transactions")
public class Transaction {
    @Id
    private Long id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "merchant_order_id")
    private String merchantOrderId;

    @Column(name = "merchant_timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime merchantTimestamp;

    @Column(name = "success_url")
    private String successURL;

    @Column(name = "failed_url")
    private String failedURL;

    @Column(name = "error_url")
    private String errorURL;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account")
    private BankAccount bankAccount;
}
