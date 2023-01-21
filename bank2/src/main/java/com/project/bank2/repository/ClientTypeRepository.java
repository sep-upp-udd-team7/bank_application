package com.project.bank2.repository;

import com.project.bank2.model.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientTypeRepository extends JpaRepository<ClientType, Long> {
    ClientType findByName(String clientTypeName);
}
