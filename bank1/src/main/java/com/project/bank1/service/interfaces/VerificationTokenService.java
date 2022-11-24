package com.project.bank1.service.interfaces;

import com.project.bank1.model.VerificationToken;

public interface VerificationTokenService {
    void saveVerificationToken(VerificationToken verificationToken);
    VerificationToken findVerificationTokenByToken(String token);
}
