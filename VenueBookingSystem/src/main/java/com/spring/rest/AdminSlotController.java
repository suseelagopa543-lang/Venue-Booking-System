package com.spring.rest;

import com.spring.Request.SlotDTO;
import com.spring.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/slots")
public class AdminSlotController {

    @Autowired
    private SlotService slotService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SlotDTO>> getAllSlots() {
        return ResponseEntity.ok(slotService.getAllSlots());
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SlotDTO>> getSlotsByDate(@RequestParam Integer venueId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(slotService.getAvailableSlotsByDate(venueId ,date));
    }

    @GetMapping("/by-venue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SlotDTO>> getSlotsByVenue(
            @RequestParam Integer venueId) {

        return ResponseEntity.ok(slotService.getAvailableSlotsByVenue(venueId));
    }
}
