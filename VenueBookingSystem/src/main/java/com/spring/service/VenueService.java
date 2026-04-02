package com.spring.service;

import com.spring.model.Vendor;
import com.spring.model.Venue;
import com.spring.repo.VendorRepo;
import com.spring.repo.VenueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    private VenueRepo venueRepo;
    private VendorRepo vendorRepo;

    @Autowired
    public VenueService(VenueRepo venueRepo, VendorRepo vendorRepo) {
         this.vendorRepo = vendorRepo;
        this.venueRepo = venueRepo;
    }

    // Search venues by name
    public List<Venue> searchVenueByName(String name) {
        return venueRepo.findByVenueNameContainingIgnoreCase(name);
    }

    // Search venues by location
    public  List<Venue> searchVenueByLocation(String location) {
        return venueRepo.findByAreaContainingIgnoreCase(location);
    }

    // Search venues by both name and location
    public List<Venue> searchVenueByNameAndLocation(String name, String location) {
        return venueRepo.findByVenueNameContainingIgnoreCaseAndAreaContainingIgnoreCase(name, location);
    }

    // Get all venues
    public List<Venue> getAllVenues() {
        return venueRepo.findAll();
    }

    // add a new venue
    public String addVenue(Venue venue, Vendor vendor) {
        venue.setVendor(vendor);
        venueRepo.save(venue);
        return "Venue added successfully";
    }

    // Delete a venue by ID
    public String deleteVenue(Integer id) {
        Venue venue = venueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
        venueRepo.delete(venue);
        return "Venue deleted successfully";
    }

    // Update venue details (only non-null fields will be updated)
    public Venue updateVenue(Integer userId, Venue updatedVenue, Integer venueId) {
        Venue venue = venueRepo.findById(venueId).orElseThrow(() -> new RuntimeException("Venue not found with id: " + venueId));

        Vendor vendor=vendorRepo.findByUserUserId(userId);
        if(!venue.getVendor().getVendorId().equals(userId)){
            throw new RuntimeException("Unauthorized");
        }

        if(updatedVenue.getVenueName() != null){
            venue.setVenueName(updatedVenue.getVenueName());
        }

        if(updatedVenue.getSportType() != null){
            venue.setSportType(updatedVenue.getSportType());
        }

        if(updatedVenue.getCity() != null){
            venue.setCity(updatedVenue.getCity());
        }

        if(updatedVenue.getArea() != null){
            venue.setArea(updatedVenue.getArea());
        }

        if(updatedVenue.getAddress() != null){
            venue.setAddress(updatedVenue.getAddress());
        }

        if(updatedVenue.getLatitude() != null){
            venue.setLatitude(updatedVenue.getLatitude());
        }

        if(updatedVenue.getLongitude() != null){
            venue.setLongitude(updatedVenue.getLongitude());
        }

        if(updatedVenue.getPricePerHour() != null){
            venue.setPricePerHour(updatedVenue.getPricePerHour());
        }

        return venueRepo.save(venue);
    }
}
