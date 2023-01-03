package com.project.bank2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountDto {
    private Long id;
    private CreditCardDto creditCard;
    private String bankAccountNumber;
    private Double availableFunds;
    private Double reservedFunds;
}
