package com.spring.restcontroller;

import com.spring.request.SlotDTO;
import com.spring.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/{venueId}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<String> createSlot(@PathVariable Integer venueId,
            @RequestParam LocalDate date,@RequestParam LocalTime start,@RequestParam LocalTime end) {
        slotService.generateSlots(venueId,date,start,end);
        return new ResponseEntity<>("Slots are Created Successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{venueId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<SlotDTO>> getSlotByVenue(@PathVariable Integer venueId) {
        List<SlotDTO> slots = slotService.getAvailableSlotsByVenue(venueId);
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }

    @GetMapping("/get-slots-byDate/{venueId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<SlotDTO>> getSlotsByDate(@PathVariable Integer venueId, @RequestParam LocalDate date) {
        List<SlotDTO> slots = slotService.getAvailableSlotsByDate(venueId,date);
        return new ResponseEntity<>(slots, HttpStatus.OK);
    }


}
