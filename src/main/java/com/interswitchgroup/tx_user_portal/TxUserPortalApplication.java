package com.interswitchgroup.tx_user_portal;

import com.interswitchgroup.tx_user_portal.repositories.OrganizationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TxUserPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TxUserPortalApplication.class, args);
		System.out.println("Hello World");
	}
}
