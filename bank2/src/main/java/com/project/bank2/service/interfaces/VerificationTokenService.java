package com.project.bank2.service.interfaces;

import com.project.bank2.model.VerificationToken;

public interface VerificationTokenService {
    void saveVerificationToken(VerificationToken verificationToken);
    VerificationToken findVerificationTokenByToken(String token);
}
