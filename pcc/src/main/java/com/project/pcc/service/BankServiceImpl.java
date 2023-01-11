package com.project.pcc.service;

import com.project.pcc.model.Bank;
import com.project.pcc.repository.BankRepository;
import com.project.pcc.service.interfaces.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    BankRepository bankRepository;

    @Override
    public List<Bank> findAll() {
        return bankRepository.findAll();
    }
}
