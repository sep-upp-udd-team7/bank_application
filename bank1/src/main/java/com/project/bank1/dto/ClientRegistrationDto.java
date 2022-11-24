package com.project.bank1.dto;

import lombok.Data;

@Data
public class ClientRegistrationDto {
    private String name;
    private String email;
    private String password;
    private String reenteredPassword;
    private boolean isCompany;
}
