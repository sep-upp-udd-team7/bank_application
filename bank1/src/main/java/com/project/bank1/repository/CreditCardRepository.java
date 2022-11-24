package com.project.bank1.repository;

import com.project.bank1.model.ClientType;
import com.project.bank1.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
}
