package com.spring.repo;

import com.spring.model.Booking;
import com.spring.model.BookingStatus;
import com.spring.model.Status;
import com.spring.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking,Integer>
{
    List<Booking> findByUser_UserId(Integer userId);

    @Query("""
    SELECT DISTINCT b FROM Booking b
    JOIN b.slots s
    JOIN s.venue v
    JOIN v.vendor vendor
    WHERE vendor.vendorId= :vendorId AND
    vendor.vendorStatus = :status
    """)
    List<Booking> findActiveVendorBookings(@Param("vendorId") Integer vendorId,
            @Param("status") Status status);

    boolean existsByVenueAndBookingStatus(Venue venue, BookingStatus status);


    Optional<Booking> findByBookingIdAndUser_UserId(Integer bookingId, Integer userID);


   boolean existsByUser_UserIdAndVenue_VenueIdAndBookingStatus(
           Integer userId,
           Integer venueId,
           BookingStatus status
   );

    Optional<Booking> findTopByVenue_VenueId(Integer venueId);
}
