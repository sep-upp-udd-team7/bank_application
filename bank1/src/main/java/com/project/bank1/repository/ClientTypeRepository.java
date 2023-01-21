package com.project.bank1.repository;

import com.project.bank1.model.Client;
import com.project.bank1.model.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientTypeRepository extends JpaRepository<ClientType, Long> {
    ClientType findByName(String clientTypeName);
}
