package com.spring.rest;

import com.spring.model.Booking;
import com.spring.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    @Autowired
    private BookingService bookingService;


    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PutMapping("/admin/bookings/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cancelBooking(@PathVariable Integer id) {
        bookingService.adminCancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
