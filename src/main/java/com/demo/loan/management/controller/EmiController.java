package com.demo.loan.management.controller;

import com.demo.loan.management.dto.EmiPaymentRequestDTO;
import com.demo.loan.management.model.Emi;
import com.demo.loan.management.service.EmiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emis")
@RequiredArgsConstructor
@Tag(name = "EMI Controller", description = "Manage EMI payments and schedules")
public class EmiController {

    private final EmiService emiService;

    @PostMapping("/create")
    @Operation(summary = "Create EMI", description = "Creates EMI schedule (typically used after loan approval)")
    public ResponseEntity<Emi> createEmi(@RequestBody EmiPaymentRequestDTO emiDTO) {
        Emi emi = emiService.createEmi(emiDTO);
        return ResponseEntity.ok(emi);
    }

    @PutMapping("/pay/{emiId}")
    @Operation(summary = "Pay EMI", description = "Pay an EMI by its ID")
    public ResponseEntity<String> payEmi(@PathVariable Long emiId, @RequestBody EmiPaymentRequestDTO paymentRequest) {
        try {
            emiService.payEmi(emiId, paymentRequest);
            return ResponseEntity.ok("EMI payment successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment failed: " + e.getMessage());
        }
    }

    @GetMapping("/loan/{loanId}")
    @Operation(summary = "Get EMIs by loan", description = "Fetches EMI schedule for a specific loan")
    public ResponseEntity<List<Emi>> getEmisByLoanId(@PathVariable Long loanId) {
        List<Emi> emis = emiService.getEmisByLoanId(loanId);
        return ResponseEntity.ok(emis);
    }

    @GetMapping("/history")
    @Operation(summary = "Get EMI history", description = "Fetches EMI payment history for the logged-in user")
    public ResponseEntity<List<Emi>> getEmiHistoryForUser() {
        List<Emi> emis = emiService.getEmiHistoryForUser();
        return ResponseEntity.ok(emis);
    }
}
