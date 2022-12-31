package com.project.bank1.dto;

import com.project.bank1.model.ClientType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientDto {
    private Long id;
    private String merchantId;
    private String merchantPassword;
    private String name;
    private String email;
    private BankAccountDto bankAccount;
}
