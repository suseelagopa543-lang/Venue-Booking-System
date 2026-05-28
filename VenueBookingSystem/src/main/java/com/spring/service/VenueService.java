package com.spring.service;

import com.spring.request.GeoResponse;
import com.spring.request.VenueDTO;
import com.spring.exception.ResourceNotFoundException;
import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import com.spring.repo.VenueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class VenueService {

    private VenueRepo venueRepo;
    private VendorRepo vendorRepo;
    private UserRepo userRepo;
    private GeoService geoService;
    private BookingRepo bookingRepo;

    public VenueDTO mapToDTO(Venue venue) {
        VenueDTO dto = new VenueDTO();
        dto.setVenueId(venue.getVenueId());
        dto.setVenueName(venue.getVenueName());
        dto.setSportType(venue.getSportType());
        dto.setAddress(venue.getAddress());
        dto.setCity(venue.getCity());
        dto.setArea(venue.getArea());
        dto.setPin(venue.getPin());
        dto.setPricePerHour(venue.getPricePerHour());
        dto.setVenueStatus(venue.getVenueStatus());
        return dto;
    }

    @Autowired
    public VenueService(VenueRepo venueRepo, VendorRepo vendorRepo,UserRepo userRepo,GeoService geoService,BookingRepo bookingRepo) {
         this.vendorRepo = vendorRepo;
        this.venueRepo = venueRepo;
        this.userRepo  = userRepo;
        this.geoService=geoService;
        this.bookingRepo=bookingRepo;
    }

    // search venue
    public List<VenueDTO>   searchVenues(String name, String location){

        boolean hasName = name != null && !name.isBlank();
        boolean hasLocation = location != null && !location.isBlank();

        if (!hasName && !hasLocation) {
            throw new IllegalArgumentException("At least one search parameter must be provided");
        }

        List<Venue> venues;

        if (hasName && hasLocation) {
            venues = venueRepo.findByVenueNameContainingIgnoreCaseAndAreaContainingIgnoreCase(name, location);
        } else if (hasName) {
            venues = venueRepo.findByVenueNameContainingIgnoreCase(name);
        } else {
            venues = venueRepo.findByAreaContainingIgnoreCase(location);
        }

        return venues.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // add a new venue
        @Transactional
        public String addVenue(Venue venue) {

                if (venue == null) {
                    throw new RuntimeException("Venue data is required");
                }

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
                    throw new RuntimeException("User not authenticated");
                }

                String username = auth.getName();

                User user = userRepo.findActiveUserByEmail(username)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with email: " + username));

                Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Vendor not found for user with email: " + username));

                if (venue.getAddress() == null || venue.getArea() == null || venue.getCity() == null) {
                    throw new RuntimeException("Address fields are required");
                }

            String fullAddress = venue.getAddress() + ", " +
                    venue.getArea() + ", " +
                    venue.getCity() + ", " +
                    venue.getPin() + ", India";

                GeoResponse geo=geoService.getLatLng(fullAddress);

                venue.setLatitude(geo.getLat());
                venue.setLongitude(geo.getLng());

                venue.setVendor(vendor);
                venue.setVenueStatus(Status.ACTIVE);
                venueRepo.save(venue);

                return "Venue added successfully";
        }

    // Delete a venue by ID
    @Transactional
    public String deactivateVenue(Integer venueId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        String username = auth.getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + username));

        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendor not found for user with email: " + username));
        Venue venue = venueRepo
                .findByVenueIdAndVendor_VendorId(venueId, vendor.getVendorId())
                .orElseThrow(() -> new RuntimeException("Venue not found or not owned by vendor"));

        if (venue.getVenueStatus() == Status.INACTIVE) {
            throw new IllegalStateException("Venue is already inactive");
        }
        if (bookingRepo.existsByVenueAndBookingStatus(venue, BookingStatus.ACTIVE)) {
            throw new RuntimeException("Cannot deactivate venue with active bookings");
        }
        venue.setVenueStatus(Status.INACTIVE);
        venueRepo.save(venue);
        return "Venue deactivated successfully";
    }

    //update
    @Transactional
    public Venue updateVenue(Integer venueId, Venue updatedVenue) {

        if (updatedVenue == null) {
            throw new RuntimeException("Venue data is required");
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        String username = auth.getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Venue venue = venueRepo
                .findByVenueIdAndVendor_VendorId(venueId, vendor.getVendorId())
                .orElseThrow(() -> new RuntimeException("Venue not found or not owned by vendor"));


        if (updatedVenue.getVenueName() != null)
            venue.setVenueName(updatedVenue.getVenueName());

        if (updatedVenue.getSportType() != null)
            venue.setSportType(updatedVenue.getSportType());

        if (updatedVenue.getAddress() != null)
            venue.setAddress(updatedVenue.getAddress());

        if (updatedVenue.getArea() != null)
            venue.setArea(updatedVenue.getArea());

        if (updatedVenue.getCity() != null)
            venue.setCity(updatedVenue.getCity());

        if (updatedVenue.getPricePerHour() != null)
            venue.setPricePerHour(updatedVenue.getPricePerHour());


        if (updatedVenue.getAddress() != null ||
                updatedVenue.getArea() != null ||
                updatedVenue.getCity() != null) {

            String fullAddress = venue.getAddress() + ", " +
                    venue.getArea() + ", " +
                    venue.getCity();

            GeoResponse geo = geoService.getLatLng(fullAddress);

            venue.setLatitude(geo.getLat());
            venue.setLongitude(geo.getLng());
        }

        return venueRepo.save(venue);
    }

    public List<Venue> getAllVenues() {
        List<Venue> venues= venueRepo.findByVenueStatus(Status.ACTIVE);
        if(venues.isEmpty()){
            throw new RuntimeException("No active venues found");
        }
        return venues;
    }


    public Venue getVenueById(Integer id) {
        return venueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
    }


    public String deleteVenue(Integer id) {
        Venue venue = venueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        if (venue.getVenueStatus() == Status.INACTIVE) {
            throw new RuntimeException("Venue already deleted");
        }

        if (bookingRepo.existsByVenueAndBookingStatus(venue, BookingStatus.ACTIVE)) {
            throw new RuntimeException("Cannot delete venue with active bookings");
        }

        venue.setVenueStatus(Status.INACTIVE);
        venueRepo.save(venue);
        return "Venue deleted successfully";
    }

    @Transactional
    public Venue updateVenueByAdmin(Integer id, Venue venue) {
        Venue existingVenue = venueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        if (venue.getVenueName() != null)
            existingVenue.setVenueName(venue.getVenueName());

        if (venue.getSportType() != null)
            existingVenue.setSportType(venue.getSportType());

        if (venue.getAddress() != null)
            existingVenue.setAddress(venue.getAddress());

        if (venue.getArea() != null)
            existingVenue.setArea(venue.getArea());

        if (venue.getCity() != null)
            existingVenue.setCity(venue.getCity());

        if (venue.getPricePerHour() != null)
            existingVenue.setPricePerHour(venue.getPricePerHour());

        if(venue.getVenueStatus()!=Status.INACTIVE)
            existingVenue.setVenueStatus(venue.getVenueStatus());

        if(venue.getPin()!=null)
            existingVenue.setPin(venue.getPin());

        return venueRepo.save(existingVenue);

    }
}
