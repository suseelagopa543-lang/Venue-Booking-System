package com.spring.service;

import com.spring.exception.ResourceNotFoundException;
import com.spring.model.Slot;
import com.spring.model.SlotStatus;
import com.spring.model.Venue;
import com.spring.repo.SlotRepo;
import com.spring.repo.VenueRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public SlotService(SlotRepo slotRepo, VenueRepo venueRepo)
    {
        this.venueRepo = venueRepo;
        this.slotRepo = slotRepo;
    }
    // Create a new slot for a venue
    @Transactional
    public Slot createSlot(Integer venueId,
                           LocalDate date,
                           LocalTime start,
                           LocalTime end)
    {
        if (venueId == null || date == null || start == null || end == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        Venue venue = venueRepo.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id "+venueId));

        if (!start.isBefore(end))
        {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        boolean exists = slotRepo
                .existsByVenueIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                        venueId, date, end, start
                );

        if (exists) {
            throw new IllegalStateException("Slot overlaps with existing slot");
        }

        Slot slot = new Slot();
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        slot.setVenue(venue);
        slot.setSlotStatus(SlotStatus.AVAILABLE);

        return slotRepo.save(slot);
    }

    //Get slots by venue
    public List<Slot> getSlotsByVenue(Integer venueId) {
        if (venueId==null) {
            throw new IllegalArgumentException("Venue Id cannot be null");
        }
         return slotRepo.findByVenueId(venueId);

    }

    //get slots by date
    public List<Slot> getSlotsByDate(Integer venueId, LocalDate date) {
        if(date == null ||venueId==null){
            throw new IllegalArgumentException("Date and Venue Id cannot be null");
        }

        return  slotRepo.findByVenueIdAndDate(venueId,date);
    }

}
