package com.project.bank1.repository;

import com.project.bank1.model.Client;
import com.project.bank1.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findVerificationTokenByToken(String token);
}
