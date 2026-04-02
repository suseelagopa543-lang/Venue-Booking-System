package com.spring.rest;

import com.spring.model.Vendor;
import com.spring.model.Venue;
import com.spring.Request.VenueRequest;
import com.spring.service.VendorService;
import com.spring.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {
    private VenueService venueService;
    private VendorService vendorService;

    @Autowired
    public VenueController(VenueService venueService, VendorService vendorService) {
        this.vendorService = vendorService;
        this.venueService = venueService;
     }

     @GetMapping("/searchByName")
    public ResponseEntity<List<Venue>> searchByName(@RequestParam String name) {
        List<Venue> venues = venueService.searchVenueByName(name);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping("/searchByLocation")
    public ResponseEntity<List<Venue>> searchByLocation(@RequestParam String location) {
        List<Venue> venues = venueService.searchVenueByLocation(location);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping("/searchByNameAndLocation")
    public ResponseEntity<List<Venue>> searchByNameAndLocation(@RequestParam String name, @RequestParam String location) {
        List<Venue> venues = venueService.searchVenueByNameAndLocation(name, location);
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        return new ResponseEntity<>(venues, HttpStatus.OK);
     }

     @PostMapping("/add")
    public ResponseEntity<String> addVenue(@RequestBody VenueRequest venueRequest) {
         Vendor vendor = vendorService.getVendorById(venueRequest.getVendorId());
        String response = venueService.addVenue(venueRequest.getVenue(), vendor);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
     }

     @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVenue(@PathVariable Integer id) {
        String response = venueService.deleteVenue(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
     }
     @PutMapping("/update/{userId}/{venueId}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Integer userId, @RequestBody Venue updatedVenue, @PathVariable Integer venueId) {
        Venue venue = venueService.updateVenue(userId, updatedVenue, venueId);
        return new ResponseEntity<>(venue, HttpStatus.OK);
    }

}
