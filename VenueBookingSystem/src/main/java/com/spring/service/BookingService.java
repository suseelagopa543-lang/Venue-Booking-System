package com.spring.service;

import com.spring.Request.BookingDTO;
import com.spring.Request.SlotDTO;
import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.repo.SlotRepo;
import com.spring.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public Booking bookSlots(List<Integer> slotIds){


        if (slotIds == null || slotIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid slots");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        String username = auth.getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Slot> slots = slotRepo.findAllByIdForUpdate(slotIds);

        Integer venueId = slots.get(0).getVenue().getVenueId();

        for (Slot slot : slots) {
            if (!slot.getVenue().getVenueId().equals(venueId)) {
                throw new RuntimeException("All slots must belong to same venue");
            }
        }

        if (slots.size() != slotIds.size()) {
            throw new RuntimeException("Some slots not found");
        }

        for (Slot slot : slots) {
            if (!slot.getSlotStatus().equals(SlotStatus.AVAILABLE)) {
                throw new RuntimeException("Slot already booked or held");
            }
        }

        for (Slot slot : slots) {
            slot.setSlotStatus(SlotStatus.HELD);
        }

        slotRepo.saveAll(slots);

        for (Slot slot : slots) {
            boolean exists = slotRepo.existsByVenue_VenueIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                    venueId,
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime()

            );

            if (exists) {
                throw new RuntimeException("Slot overlap detected");
            }
        }

        Booking booking = new Booking();
        Venue venue = slots.get(0).getVenue();
        booking.setVenue(venue);
        booking.setBookingTime(LocalDateTime.now());
        booking.setSlots(slots);
        booking.setUser(user);
        booking.setBookingStatus(BookingStatus.PENDING);

        return bookingRepo.save(booking);
    }
    public BookingDTO mapToDTO(Booking booking) {

        BookingDTO dto = new BookingDTO();

        dto.setBookingId(booking.getBookingId());
        dto.setBookingStatus(booking.getBookingStatus().name());
        dto.setBookingTime(booking.getBookingTime());

        // User
        dto.setUserName(booking.getUser().getUserName());
        dto.setUserEmail(booking.getUser().getUserEmail());
        dto.setPhoneNumber(booking.getUser().getPhoneNumber());

        // Venue
        dto.setVenueName(booking.getVenue().getVenueName());
        dto.setCity(booking.getVenue().getCity());
        dto.setArea(booking.getVenue().getArea());


        // Slots
        List<SlotDTO> slotDTOs = booking.getSlots()
                .stream()
                .map(this::mapSlotToDTO)
                .toList();

        dto.setSlots(slotDTOs);

        return dto;
    }
    public SlotDTO mapSlotToDTO(Slot slot) {

        SlotDTO dto = new SlotDTO();

        dto.setSlotId(slot.getSlotId());
        dto.setDate(slot.getDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setSlotStatus(slot.getSlotStatus().name());
        return dto;
    }
    // Cancel a booking
    @Transactional
    public Booking cancelBooking(Integer bookingId) {

        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        String username = auth.getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepo
                .findByBookingIdAndUser_UserId(bookingId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Booking not found or not yours"));


        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking already cancelled");
        }

        List<Slot> slots = booking.getSlots();


        for (Slot slot : slots) {
            if (slot.getDate().isBefore(LocalDate.now())) {
                throw new IllegalStateException("Cannot cancel past bookings");
            }
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        for (Slot slot : slots) {
            slot.setSlotStatus(SlotStatus.AVAILABLE);
        }

        slotRepo.saveAll(slots);

        return bookingRepo.save(booking);
    }

    // Get booking details by ID
    public Booking getBookingById(Integer bookingId) {
        if (bookingId==null){
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }


        String email = auth.getName();

        User user = userRepo.findActiveUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        return bookingRepo
                .findByBookingIdAndUser_UserId(bookingId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

    }

    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

}
