package com.spring.service;

import com.spring.Request.SlotDTO;
import com.spring.exception.ResourceNotFoundException;
import com.spring.model.*;
import com.spring.repo.SlotRepo;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import com.spring.repo.VenueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SlotService
{
    private SlotRepo slotRepo;
    private VenueRepo venueRepo;
    private UserRepo userRepo;
    private VendorRepo vendorRepo;

    @Autowired
    public SlotService(SlotRepo slotRepo,VenueRepo venueRepo,UserRepo userRepo,VendorRepo vendorRepo)
    {

        this.slotRepo = slotRepo;
        this.venueRepo=venueRepo;
        this.userRepo=userRepo;
        this.vendorRepo=vendorRepo;
    }

    // Generate Slot
    @Transactional
    public void generateSlots(Integer venueId, LocalDate date, LocalTime start, LocalTime end) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + username));

        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendor not found for user with email: " + username));

        // ✅ 2. Fetch venue
        Venue venue = venueRepo.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        // 🔒 3. Ownership check (MAIN FIX)
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!venue.getVendor().getVendorId().equals(vendor.getVendorId())) {
            throw new RuntimeException("Unauthorized: You cannot create slots for this venue");
        }

        LocalTime current = start;

        while (current.isBefore(end)) {

            LocalTime slotEnd = current.plusHours(1);

            boolean exists = slotRepo.existsByVenue_VenueIdAndDateAndStartTimeAndEndTime(
                    venueId, date, current, slotEnd
            );

            if (exists) {
                throw new RuntimeException(
                        "Slot already exists for time: " + current + " - " + slotEnd
                );
            }

            boolean overlap = slotRepo.existsByVenue_VenueIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                    venueId,
                    date,
                    slotEnd,
                    current
            );

            if (overlap) {
                throw new RuntimeException(
                        "Slot overlaps with existing slot: " + current + " - " + slotEnd
                );
            }

            // ✅ Create slot
            Slot slot = new Slot();
            slot.setDate(date);
            slot.setStartTime(current);
            slot.setEndTime(slotEnd);
            slot.setSlotStatus(SlotStatus.AVAILABLE);
            slot.setVenue(venue);

            slotRepo.save(slot);

            current = slotEnd;
        }
    }

    //Available slots by venue
    public List<SlotDTO> getAvailableSlotsByVenue(Integer venueId) {

        if (venueId == null) {
            throw new IllegalArgumentException("Venue Id cannot be null");
        }

        List<Slot> slots= slotRepo.findByVenue_VenueIdAndSlotStatus(
                venueId,
                SlotStatus.AVAILABLE
        );

        return  slots.stream()
                .map(this::mapToDTO)
                .toList();
    }
    public SlotDTO mapToDTO(Slot slot) {
        SlotDTO dto = new SlotDTO();
        dto.setSlotId(slot.getSlotId());
        dto.setDate(slot.getDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setSlotStatus(slot.getSlotStatus().name());
        return dto;
    }

    //Available slots by Date
    public List<SlotDTO> getAvailableSlotsByDate(Integer venueId, LocalDate date) {

        if (venueId == null || date == null) {
            throw new IllegalArgumentException("Date and Venue Id cannot be null");
        }

        List<Slot> slot= slotRepo.findByVenue_VenueIdAndDateAndSlotStatusOrderByStartTimeAsc(
                venueId,
                date,
                SlotStatus.AVAILABLE
        );

        return  slot.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<SlotDTO> getAllSlots() {
        List<Slot> slots = slotRepo.findAll();

        return slots.stream()
                .map(this::mapToDTO)
                .toList();
    }
}
