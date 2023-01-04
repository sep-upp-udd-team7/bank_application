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
    public PccResponseDto checkIsValidAndRedirect(PccRequestDto dto) {

        String msg = "Starting with PCC...";
        System.out.println(msg);

        //todo: provjeriti sta jos da se cuva u pccu
        Request r = createNewRequest(dto);

        if(!checkIsValidRequest(dto)){
            msg = "Request in not valid!";
            System.out.println(msg);
            //u slucaju neispravnog zahtjeva, vraca se na banku 1
            return createPccResponse(dto);
        }

        //zahtjev se salje na banku 2 i odgovor se vraca banci1
        msg = "Sending request to Bank 2...";
        System.out.println(msg);
        return sendRequestToIssuerBank(dto).getBody();
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
        else if(dto.getMerchantOrderId() == null){
            return false;
        }
        else if(dto.getMerchantTimestamp() == null){
            return false;
        }
        else if(dto.getSuccessURL() == null){
            return false;
        }
        else if(dto.getFailedURL() == null){
            return false;
        }
        else if(dto.getErrorURL() == null){
            return false;
        }
        else{
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

    PccResponseDto createPccResponse(PccRequestDto dto){
        PccResponseDto res = new PccResponseDto();
        res.setAcquirerOrderId(dto.getAcquirerOrderId());
        res.setAcquirerTimestamp(dto.getAcquirerTimestamp());
        res.setTransactionStatus("ERROR");

        return res;
    }



}
