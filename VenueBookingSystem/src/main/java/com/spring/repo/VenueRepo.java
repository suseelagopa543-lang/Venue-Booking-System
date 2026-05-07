package com.spring.repo;

import com.spring.model.Status;
import com.spring.model.User;
import com.spring.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VenueRepo extends JpaRepository<Venue,Integer> {

    List<Venue> findByVenueNameContainingIgnoreCase(String name);
    List<Venue> findByAreaContainingIgnoreCase(String location);
    List<Venue> findByVenueNameContainingIgnoreCaseAndAreaContainingIgnoreCase(String name, String location);
    Optional<Venue> findByVenueIdAndVendor_VendorId(Integer venueId, Integer getVendorId);
    List<Venue> findByVenueStatus(Status userStatus);
}
