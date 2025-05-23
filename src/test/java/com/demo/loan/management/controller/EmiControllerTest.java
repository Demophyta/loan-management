package com.demo.loan.management.controller;

import com.demo.loan.management.dto.EmiPaymentRequestDTO;
import com.demo.loan.management.model.Emi;
import com.demo.loan.management.service.EmiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmiControllerTest {

    private EmiService emiService;
    private EmiController emiController;

    @BeforeEach
    void setUp() {
        emiService = mock(EmiService.class);
        emiController = new EmiController(emiService);
    }

    @Test
    void testCreateEmi() {
        EmiPaymentRequestDTO requestDTO = new EmiPaymentRequestDTO();
        Emi mockEmi = new Emi();
        when(emiService.createEmi(requestDTO)).thenReturn(mockEmi);

        ResponseEntity<Emi> response = emiController.createEmi(requestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockEmi, response.getBody());
        verify(emiService).createEmi(requestDTO);
    }

    @Test
    void testPayEmi_Success() {
        Long emiId = 1L;
        EmiPaymentRequestDTO requestDTO = new EmiPaymentRequestDTO();
        when(emiService.payEmi(emiId, requestDTO)).thenReturn("Payment successful");

        ResponseEntity<String> response = emiController.payEmi(emiId, requestDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("EMI payment successful", response.getBody()); // Controller hardcodes this
        verify(emiService).payEmi(emiId, requestDTO);
    }

    @Test
    void testPayEmi_Failure() {
        Long emiId = 1L;
        EmiPaymentRequestDTO requestDTO = new EmiPaymentRequestDTO();
        when(emiService.payEmi(emiId, requestDTO)).thenThrow(new RuntimeException("Insufficient funds"));

        ResponseEntity<String> response = emiController.payEmi(emiId, requestDTO);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Payment failed:"));
        verify(emiService).payEmi(emiId, requestDTO);
    }

    @Test
    void testGetEmisByLoanId() {
        Long loanId = 1L;
        List<Emi> mockList = Collections.singletonList(new Emi());
        when(emiService.getEmisByLoanId(loanId)).thenReturn(mockList);

        ResponseEntity<List<Emi>> response = emiController.getEmisByLoanId(loanId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockList, response.getBody());
        verify(emiService).getEmisByLoanId(loanId);
    }

    @Test
    void testGetEmiHistoryForUser() {
        List<Emi> mockHistory = Collections.singletonList(new Emi());
        when(emiService.getEmiHistoryForUser()).thenReturn(mockHistory);

        ResponseEntity<List<Emi>> response = emiController.getEmiHistoryForUser();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockHistory, response.getBody());
        verify(emiService).getEmiHistoryForUser();
    }
}
