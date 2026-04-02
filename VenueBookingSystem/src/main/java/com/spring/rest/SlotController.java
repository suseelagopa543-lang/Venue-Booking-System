package com.spring.rest;

import com.spring.model.Slot;
import com.spring.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/slots")
public class SlotController {
    private SlotService slotService;

    @Autowired
    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @PostMapping("/venues/{venueId}/slots")
    public ResponseEntity<Slot> createSlot(@PathVariable Integer venueId,@RequestParam LocalDate date,@RequestParam LocalTime start,@RequestParam LocalTime end) {
        Slot slot=slotService.createSlot(venueId, date, start, end);
        return new ResponseEntity<>(slot, HttpStatus.CREATED);
    }

    @GetMapping("/venues/{venueId}/slots")
    public ResponseEntity<List<Slot>> getSlotByVenue(@PathVariable Integer venueId) {
        List<Slot> slots = slotService.getSlotsByVenue(venueId);
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }

    @GetMapping("/venues/{venueId}/slots/by-date")
    public ResponseEntity<List<Slot>> getSlotsByDate(@PathVariable Integer venueId, @RequestParam LocalDate date) {
        List<Slot> slots = slotService.getSlotsByDate(venueId, date);
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }

}
