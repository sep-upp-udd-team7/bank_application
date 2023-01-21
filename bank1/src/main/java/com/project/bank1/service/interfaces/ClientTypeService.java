package com.project.bank1.service.interfaces;

import com.project.bank1.model.ClientType;

public interface ClientTypeService {
    ClientType findClientTypeByName(String clientTypeName);
}
