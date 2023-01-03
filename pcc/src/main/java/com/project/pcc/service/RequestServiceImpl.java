package com.project.pcc.service;

import com.project.pcc.dto.PccRequestDto;
import com.project.pcc.dto.PccResponseDto;
import com.project.pcc.model.Request;
import com.project.pcc.repository.RequestRepository;
import com.project.pcc.service.interfaces.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private Environment environment;

    @Autowired
    private RequestRepository requestRepository;

    @Override
    public Boolean checkIsValidAndRedirect(PccRequestDto dto) {

        Request r = createNewRequest(dto);

        if(!checkIsValidRequest(dto)){
            return false;
        }

        //todo: poslati zahtjev na banku 2
        PccResponseDto responseFromIssuerBank = sendRequestToIssuerBank(dto).getBody();
        System.out.println("Ovo je status transakcije sa banke2 : " + responseFromIssuerBank.getTransactionStatus());


        //TODO: vrati podatke banci 1
        return true;
    }

    Boolean checkIsValidRequest(PccRequestDto dto){
        System.out.println(dto.getAmount());

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
        else if(dto.getAmount() == null){
            return false;
        }
        else{ //TODO: dodati else if za ostala polja
            return true;
        }
    }


    private ResponseEntity<PccResponseDto> sendRequestToIssuerBank(PccRequestDto requestForIssuerBank) {

        System.out.println("na ovaj url se salje:" + environment.getProperty("issuer-bank.pay"));
        ResponseEntity<PccResponseDto> issuerBankResponse = WebClient.builder()
                .build().post()
                .uri(environment.getProperty("issuer-bank.pay"))
                .body(BodyInserters.fromValue(requestForIssuerBank))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(PccResponseDto.class)
                .block();
        return issuerBankResponse;
    }

    Request createNewRequest(PccRequestDto dto){
        Request r = new Request();
        r.setAcquirerOrderId(dto.getAcquirerOrderId());
        r.setAcquirerTimestamp(dto.getAcquirerTimestamp());
        r.setCardHolderName(dto.getCardHolderName());
        r.setCvv(dto.getCvv());
        r.setPan(dto.getPan());
        r.setMm(dto.getMm());
        r.setYy(dto.getYy());
        r.setAmount(dto.getAmount());

        return requestRepository.save(r);
    }
}
