package com.spring.restcontroller;

import com.spring.model.Payment;
import com.spring.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllUserPayments(@PathVariable Integer userId) {
        return ResponseEntity.ok(paymentService.getAllUserPayments(userId));
    }

    // 🔹 All vendor payments
    @GetMapping("/vendors/{vendorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllVendorPayments(@PathVariable Integer  vendorId) {
        return ResponseEntity.ok(paymentService.getAllVendorPayments(vendorId));
    }

    // 🔹 All venue payments
    @GetMapping("/venues/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllVenuePayments(@PathVariable Integer venueId) {
        return ResponseEntity.ok(paymentService.getAllVenuePayments(venueId));
    }



}
