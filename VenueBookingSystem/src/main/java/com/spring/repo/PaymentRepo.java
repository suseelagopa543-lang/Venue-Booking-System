package com.spring.repo;

import com.spring.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment,Integer> {

    List<Payment> findByBooking_User_UserId(Integer userId);

    @Query("""
    SELECT v.venueId, v.venueName, SUM(p.amount)
    FROM Payment p
    JOIN p.booking b
    JOIN b.venue v
    WHERE v.vendor.vendorId = :vendorId
    GROUP BY v.venueId, v.venueName
    """)
    List<Object[]> getVendorRevenueByVenue(Integer vendorId);


    List<Payment> findByBooking_Venue_VenueId(Integer venueId);

    boolean existsByBooking_BookingId(Integer bookingId);

    //List<Payment> findByBooking_User_UserId(Integer userId);

    //List<Payment> findByBooking_Venue_VenueId(Long venueId);

    List<Payment> findByBooking_Venue_Vendor_VendorId(Integer vendorId);
}
