package com.project.bank1.service.interfaces;

import com.project.bank1.dto.*;

public interface ClientService {
    UserTokenStateDto registerClient(ClientRegistrationDto dto) throws Exception;

    UserTokenStateDto login(LoginDto dto) throws Exception;


}
