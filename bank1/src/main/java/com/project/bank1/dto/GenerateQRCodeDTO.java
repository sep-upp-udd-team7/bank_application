package com.project.bank1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateQRCodeDTO {
    private Double amount;
    private String receiver;
    private String accountNumber;
    private String idTransaction;
}
