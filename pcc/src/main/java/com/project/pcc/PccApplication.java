package com.project.pcc;

import com.project.pcc.model.Bank;
import com.project.pcc.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.HashSet;

@SpringBootApplication
public class PccApplication implements CommandLineRunner {
	@Autowired
	private Environment environment;
	@Autowired
	private BankRepository bankRepository;

	public static void main(String[] args) {
		SpringApplication.run(PccApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		insert();
	}

	private void insert() {
		Bank b1 = new Bank();
		b1.setId(1L);
		b1.setName("Bank 1");
		b1.setUrl(environment.getProperty("bank1-application.backend"));
		b1.setPan(environment.getProperty("bank1-application.pan"));
		bankRepository.save(b1);

		Bank b2 = new Bank();
		b2.setId(2L);
		b2.setName("Bank 2");
		b2.setUrl(environment.getProperty("bank2-application.backend"));
		b2.setPan(environment.getProperty("bank2-application.pan"));
		bankRepository.save(b2);
	}
}
