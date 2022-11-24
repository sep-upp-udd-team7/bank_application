package com.project.bank1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="bank_accounts")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;  // fizicka kartica

    @Column(name = "bank_account_number")
    private String bankAccountNumber;   // br. ziro racuna

    @Column(name = "available_funds")
    private Double availableFunds;

    @Column(name = "reserved_funds")
    private Double reservedFunds;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;   // 1 klijent moze da ima vise racuna

    @OneToMany(mappedBy="bankAccount")
    private List<Transaction> transactions;
}
