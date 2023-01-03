package com.project.pcc.controller;

import com.project.pcc.dto.PccRequestDto;
import com.project.pcc.service.interfaces.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
public class RequestController {

    @Autowired
    RequestService requestService;

    @RequestMapping(method = RequestMethod.POST, value = "/validateAndRedirect", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateAndRedirect(@RequestBody PccRequestDto dto) {

        try {
            return new ResponseEntity<>(requestService.checkIsValidAndRedirect(dto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
