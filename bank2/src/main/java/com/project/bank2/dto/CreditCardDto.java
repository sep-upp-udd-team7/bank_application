package com.project.bank2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreditCardDto {
    private Long id;
    private String cardHolderName;
    private String pan;
    private String mm;
    private String yy;
    private String cvv;
}
