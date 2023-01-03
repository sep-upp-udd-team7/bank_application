package com.project.pcc.service.interfaces;

import com.project.pcc.dto.PccRequestDto;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl implements RequestService{

    @Override
    public Boolean checkIsValidAndRedirect(PccRequestDto dto) {
        if(!checkIsValidRequest(dto)){
            return false;
        }

        //todo: poslati zahtjev na banku 2


        return null;
    }

    Boolean checkIsValidRequest(PccRequestDto dto){
        if(dto.getAcquirerOrderId() == null){
            return false;
        }
        else if(dto.getAcquirerTimestamp() == null){
            return false;
        }
        else if(dto.getCvv() == null){
            return false;
        }
        else if(dto.getCardHolderName() == null){
            return false;
        }
        else if(dto.getPan() == null){
            return false;
        }
        else if(dto.getMm() == null){
            return false;
        }
        else if(dto.getYy() == null){
            return false;
        }
        else if(dto.getPaymentId() == null){ //todo: potencijalno obrisati
            return false;
        }
        else{
            return true;
        }
    }
}
