package com.spring.rest;

import com.spring.model.Payment;
import com.spring.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllUserPayments() {
        return ResponseEntity.ok(paymentService.getAllUserPayments());
    }

    // 🔹 All vendor payments
    @GetMapping("/vendors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllVendorPayments() {
        return ResponseEntity.ok(paymentService.getAllVendorPayments());
    }

    // 🔹 All venue payments
    @GetMapping("/venues")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllVenuePayments() {
        return ResponseEntity.ok(paymentService.getAllVenuePayments());
    }



}
