package com.spring.service;

import com.spring.exception.ResourceNotFoundException;
import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.repo.SlotRepo;
import com.spring.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private BookingRepo bookingRepo;
    private SlotRepo slotRepo;
    private UserRepo userRepo;

    @Autowired
    public BookingService(BookingRepo bookingRepo, SlotRepo slotRepo, UserRepo userRepo) {
        this.userRepo = userRepo;
        this.slotRepo = slotRepo;
        this.bookingRepo = bookingRepo;
    }

    //Book slots for a venue
    @Transactional
    public Booking bookSlot(Integer userId, Integer slotId){
        if (userId == null || slotId == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        Slot slot = slotRepo.findByIdForUpdate(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));

        if (slot.getSlotStatus() == SlotStatus.BOOKED) {
            throw new IllegalStateException("Slot is not available for booking");
        }



        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        slot.setSlotStatus(SlotStatus.BOOKED);

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setSlot(slot);
        booking.setUser(user);
        booking.setBookingStatus(BookingStatus.BOOKED);

        return bookingRepo.save(booking);

    }

    // Cancel a booking
    @Transactional
    public Booking cancelBooking(Integer bookingId) {
        if (bookingId== null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        if(booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (booking.getSlot().getDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot cancel past bookings");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);
        Slot slot = booking.getSlot();
        slot.setSlotStatus(SlotStatus.AVAILABLE);
        return booking;

    }

    // Get booking details by ID
    public Booking getBookingById(Integer bookingId) {
        if (bookingId==null){
            throw new IllegalArgumentException("Booking ID cannot be null");
        }
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    // Get all bookings for a user
    public List<Booking> getUserBookings(Integer userId) {
        if (userId==null){
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return bookingRepo.findByUser_UserId(userId);
    }
}
