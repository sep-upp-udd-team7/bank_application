package com.project.bank2.service;

import com.project.bank2.model.ClientType;
import com.project.bank2.repository.ClientTypeRepository;
import com.project.bank2.service.interfaces.ClientTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientTypeServiceImpl implements ClientTypeService {
    @Autowired
    private ClientTypeRepository clientTypeRepository;

    @Override
    public ClientType findClientTypeByName(String clientTypeName) {
        return clientTypeRepository.findByName(clientTypeName);
    }
}
