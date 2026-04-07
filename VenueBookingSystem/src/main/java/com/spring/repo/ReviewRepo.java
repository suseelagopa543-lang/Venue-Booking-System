package com.spring.repo;

import com.spring.model.Review;
import com.spring.model.User;
import com.spring.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review,Integer> {
    boolean existsByBooking_BookingId(Integer bookingId);
   Optional<Review> findByBooking_BookingId(Integer bookingId);


    List<Review> findByVenue_VenueId(Integer venueId);
}
