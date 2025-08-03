package com.prismo.accountapi.controllor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismo.accountapi.controller.AccountController;
import com.prismo.accountapi.dto.AccountRequestDTO;
import com.prismo.accountapi.dto.AccountResponseDTO;
import com.prismo.accountapi.exception.AccountNotFoundException;
import com.prismo.accountapi.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountRequestDTO validAccountRequest;
    private AccountResponseDTO accountResponse;

    @BeforeEach
    void setup() {
        validAccountRequest = new AccountRequestDTO();
        validAccountRequest.setDocument_number("12345678901");

        accountResponse = new AccountResponseDTO(1L, "12345678901");
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        Mockito.when(accountService.createAccount(any(AccountRequestDTO.class)))
                .thenReturn(accountResponse);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAccountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678901"));
    }

    @Test
    void testGetAccountById_Success() throws Exception {
        Mockito.when(accountService.getAccountById(eq(1L)))
                .thenReturn(accountResponse);

        mockMvc.perform(get("/accounts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.document_number").value("12345678901"));
    }

    @Test
    void testGetAccountById_NotFound() throws Exception {
        Mockito.when(accountService.getAccountById(eq(999L)))
                .thenThrow(new AccountNotFoundException("Account not found with ID: 999"));

        mockMvc.perform(get("/accounts/999")
                        .accept(MediaType.TEXT_PLAIN)) // since your controller returns message String on error
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found with ID: 999"));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/accounts/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account API is running!"));
    }
}
