package com.project.bank1;

import com.project.bank1.model.ClientType;
import com.project.bank1.repository.ClientTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Bank1Application implements CommandLineRunner {
	@Autowired
	private ClientTypeRepository clientTypeRepository;

	public static void main(String[] args) {
		SpringApplication.run(Bank1Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		insert();
	}

	private void insert() {
		ClientType clientType = new ClientType();
		clientType.setName("ROLE_CLIENT");
		clientTypeRepository.save(clientType);

		ClientType companyType = new ClientType();
		companyType.setName("ROLE_COMPANY");
		clientTypeRepository.save(companyType);
	}

}
