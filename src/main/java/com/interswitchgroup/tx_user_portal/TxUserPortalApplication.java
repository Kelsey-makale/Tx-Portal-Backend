package com.interswitchgroup.tx_user_portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TxUserPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TxUserPortalApplication.class, args);
		System.out.println("Hello World");
	}

}
