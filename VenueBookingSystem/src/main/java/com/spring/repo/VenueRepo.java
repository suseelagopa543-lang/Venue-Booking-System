package com.spring.repo;

import com.spring.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepo extends JpaRepository<Venue,Integer> {

    List<Venue> findByVenueNameContainingIgnoreCase(String name);
    List<Venue> findByAreaContainingIgnoreCase(String location);
    List<Venue> findByVenueNameContainingIgnoreCaseAndAreaContainingIgnoreCase(String name, String location);
}
