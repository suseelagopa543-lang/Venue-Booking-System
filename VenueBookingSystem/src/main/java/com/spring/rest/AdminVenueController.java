package com.spring.rest;

import com.spring.model.Venue;
import com.spring.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/venues")
public class AdminVenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping("/admin/venues")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    @GetMapping("/admin/venues/{id}")
    public Venue getVenue(@PathVariable Integer id) {
        return venueService.getVenueById(id);
    }

    @DeleteMapping("/admin/venues/{id}")
    public String deleteVenue(@PathVariable Integer id) {
        return venueService.deleteVenue(id);
    }

    @PutMapping("/admin/venues/{id}")
    public Venue updateVenue(@PathVariable Integer id,
                             @RequestBody Venue venue) {
        return venueService.updateVenueByAdmin(id, venue);
    }

}
