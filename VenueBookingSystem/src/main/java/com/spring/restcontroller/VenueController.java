package com.spring.restcontroller;

import com.spring.request.VenueDTO;
import com.spring.model.Venue;
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

    @Autowired
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
     }

    @GetMapping
    public ResponseEntity<List<VenueDTO>> searchVenues(String name,String location){

        List<VenueDTO> venues=venueService.searchVenues(name, location);
        return new ResponseEntity<>(venues,HttpStatus.OK);

    }
     @PostMapping("/add")
    public ResponseEntity<String> addVenue(@RequestBody Venue venue) {
         String response=venueService.addVenue(venue);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
     }

     @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVenue(@PathVariable Integer id) {
        String response = venueService.deactivateVenue(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
     }
     @PutMapping("/{venueId}")
    public ResponseEntity<Venue> updateVenue( @RequestBody Venue updatedVenue, @PathVariable Integer venueId) {
        Venue venue = venueService.updateVenue(venueId, updatedVenue);
        return new ResponseEntity<>(venue, HttpStatus.OK);
    }

}
