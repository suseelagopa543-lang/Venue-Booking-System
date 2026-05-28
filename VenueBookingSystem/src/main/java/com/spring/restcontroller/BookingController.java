package com.spring.restcontroller;

import com.spring.request.BookingDTO;
import com.spring.model.Booking;
import com.spring.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDTO> bookSlot(@RequestParam  List<Integer>  slotIds) {
        Booking booking = bookingService.bookSlots(slotIds);

        BookingDTO dto = bookingService.mapToDTO(booking);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }

    @PostMapping("/cancel/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Integer bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        BookingDTO dto = bookingService.mapToDTO(booking);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Integer bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        BookingDTO dto = bookingService.mapToDTO(booking);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


}
