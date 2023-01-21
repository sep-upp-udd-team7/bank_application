package com.project.bank1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_cards")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
