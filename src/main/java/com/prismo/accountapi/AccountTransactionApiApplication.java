package com.prismo.accountapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AccountTransactionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountTransactionApiApplication.class, args);
	}

}
