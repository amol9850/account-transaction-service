package com.prismo.accountapi;

import com.prismo.accountapi.dto.AccountRequestDTO;
import com.prismo.accountapi.dto.AccountResponseDTO;
import com.prismo.accountapi.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class AccountTransactionApiApplicationTests {

	@Autowired
	private AccountService accountService;

	@Test
	void contextLoads() {
		assertNotNull(accountService);
	}

	@Test
	void testCreateAccount() {
		AccountRequestDTO request = new AccountRequestDTO("55555555555");
		AccountResponseDTO response = accountService.createAccount(request);

		assertNotNull(response);
		assertNotNull(response.getAccount_id());
		assertEquals("55555555555", response.getDocument_number());
	}

}
