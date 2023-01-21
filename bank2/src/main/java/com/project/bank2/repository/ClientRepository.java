package com.project.bank2.repository;

import com.project.bank2.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByEmail(String email);

    Client findByMerchantId(String merchantId);
}
