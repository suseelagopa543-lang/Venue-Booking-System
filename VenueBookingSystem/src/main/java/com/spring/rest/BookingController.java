package com.spring.rest;

import com.spring.model.Booking;
import com.spring.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Booking")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> bookSlot(@RequestParam Integer userId,@RequestParam  Integer slotId) {
       Booking booking = bookingService.bookSlot(userId, slotId);
       return new ResponseEntity<>(booking, HttpStatus.CREATED);

    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Integer bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable Integer bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Integer userId) {
        List<Booking> bookings = bookingService.getUserBookings(userId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

}
