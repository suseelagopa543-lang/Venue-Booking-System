package com.spring.repo;

import com.spring.model.Review;
import com.spring.model.User;
import com.spring.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review,Integer> {
    boolean existsByBookingBookingId(Integer bookingId);
   Optional<User> findUserByBookingId(Integer bookingId);
    Optional<Venue> findVenueByBookingId(Integer bookingId);

    List<Review> findByVenueVenueId(Integer venueId);
}
