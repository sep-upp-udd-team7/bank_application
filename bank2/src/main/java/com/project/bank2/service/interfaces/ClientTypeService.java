package com.project.bank2.service.interfaces;

import com.project.bank2.model.ClientType;

public interface ClientTypeService {
    ClientType findClientTypeByName(String clientTypeName);
}
