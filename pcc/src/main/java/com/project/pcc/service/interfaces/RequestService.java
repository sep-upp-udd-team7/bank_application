package com.project.pcc.service.interfaces;

import com.project.pcc.dto.PccRequestDto;
import com.project.pcc.dto.PccResponseDto;

public interface RequestService {
    PccResponseDto checkIsValidAndRedirect(PccRequestDto dto);
}
