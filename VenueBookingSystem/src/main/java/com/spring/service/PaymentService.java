package com.spring.service;

import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService{

    private PaymentRepo paymentRepo;
    private BookingRepo bookingRepo;

    @Autowired
    public PaymentService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    //Make payment
    @Transactional
    public Payment makePayment(Integer bookingId, Double amount, PaymentMethod paymentMethod)
    {
        if (bookingId == null || amount == null || amount <= 0|| paymentMethod == null) {
            throw new IllegalArgumentException("Invalid Payment details");
        }

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

        if(booking.getBookingStatus()== BookingStatus.CANCELLED){
            throw new IllegalStateException("Cannot make payment for a cancelled booking");
        }
        if(paymentRepo.existsByBookingId(bookingId)){
            throw new IllegalStateException("Payment already exists for this booking");
        }

        Payment payment = new Payment();
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setBooking(booking);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        return paymentRepo.save(payment);
    }

    //Get payment details by booking ID
    public Payment getPaymentDetails(Integer bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Invalid booking ID");
        }

        return paymentRepo.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking id: " + bookingId));
    }

}
