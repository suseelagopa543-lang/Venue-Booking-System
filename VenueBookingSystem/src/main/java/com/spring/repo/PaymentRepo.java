package com.spring.repo;

import com.spring.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment,Integer> {
    boolean existsByBooking_BookingId(Integer bookingId);

    Optional<Payment> findByBookingBookingId(Integer bookingId);
}
