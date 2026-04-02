package com.spring.rest;

import com.spring.model.Payment;
import com.spring.Request.PaymentRequest;
import com.spring.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> makePayment(@RequestBody PaymentRequest paymentRequest)
    {
            Payment payment=paymentService.makePayment(paymentRequest.getBookingId(),paymentRequest.getAmount(),paymentRequest.getPaymentMethod());
            return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Payment> getPaymentDetails(@PathVariable Integer bookingId)
    {
        Payment payment=paymentService.getPaymentDetails(bookingId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

}
