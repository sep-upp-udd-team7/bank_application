package com.project.pcc.service.interfaces;

import com.project.pcc.dto.PccRequestDto;

public interface RequestService {
    Boolean checkIsValidAndRedirect(PccRequestDto dto);
}
