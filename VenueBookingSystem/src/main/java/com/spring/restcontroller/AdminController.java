package com.spring.restcontroller;

import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.request.ReviewRequest;
import com.spring.request.SlotDTO;
import com.spring.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController
{

    @Autowired
    private VenueService venueService;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @Autowired
    private SlotService slotService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    //Get all venues
    @GetMapping("/venues")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Venue> getAllVenues()
    {
        return venueService.getAllVenues();
    }

    //Get venue by id
    @GetMapping("/venues/{id}")
    public Venue getVenue(@PathVariable Integer id) {
        return venueService.getVenueById(id);
    }

    //Delete venue by id
    @DeleteMapping("/venues/{id}")
    public String deleteVenue(@PathVariable Integer id) {
        return venueService.deleteVenue(id);
    }

    //Update venue
    @PutMapping("/venues/{id}")
    public Venue updateVenue(@PathVariable Integer id,
                             @RequestBody Venue venue)
    {
        return venueService.updateVenueByAdmin(id, venue);
    }

    //Get all vendors
    @GetMapping("/vendor/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    //Approve vendor
    @PutMapping("/vendor/{vendorId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveVendor(@PathVariable Integer vendorId) {
        return vendorService.approveVendor(vendorId);
    }

    //Reject vendor
    @PutMapping("/vendor/{vendorId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejectVendor(@PathVariable Integer vendorId) {
        return vendorService.rejectVendor(vendorId);
    }

    //Deactivate vendor
    @PutMapping("/vendor/{vendorId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateVendor(@PathVariable Integer vendorId) {
        return vendorService.deactivateVendorByAdmin(vendorId);
    }

    //Get vendor bookings
    @GetMapping("/vendor/{vendorId}/booking")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getVendorBookings(@PathVariable Integer vendorId) {
        return vendorService.getVendorBookingsByAdmin(vendorId);
    }

    //Update vendor details
    @PutMapping("/vendor/{vendorId}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateVendor(@PathVariable Integer vendorId, @RequestBody Vendor updatedVendor) {
        return vendorService.updateVendorByAdmin(vendorId, updatedVendor);
    }

    //Get vendor by id
    @GetMapping("/vendor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Vendor getVendorById(@PathVariable Integer id) {
        return vendorService.getVendorById(id);
    }

    //Get all users
    @GetMapping("/user/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //Update user details
    @PutMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserByAdmin(@PathVariable Integer userId, @RequestBody User updatedUser) {
        User user = userService.updateUserByAdmin(userId,updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //Delete user by id
    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        String response = userService.deleteUserById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Get all slots
    @GetMapping("/slot/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SlotDTO>> getAllSlots() {
        return ResponseEntity.ok(slotService.getAllSlots());
    }

    //Get slots by date and venue
    @GetMapping("/slot/by-date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SlotDTO>> getSlotsByDate(@RequestParam Integer venueId,
                                                        @RequestParam LocalDate date) {

        return ResponseEntity.ok(slotService.getAvailableSlotsByDate(venueId ,date));
    }

    //Get slots by venue
    @GetMapping("/slot/by-venue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SlotDTO>> getSlotsByVenue(
            @RequestParam Integer venueId) {

        return ResponseEntity.ok(slotService.getAvailableSlotsByVenue(venueId));
    }

    //Get all reviews
    @GetMapping("/review/all")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    //Delete review by id
    @DeleteMapping("/review/delete/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }

    //Update review by id
    @PutMapping("/review/update/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<Review> updateReview(@PathVariable Integer reviewId, @RequestBody ReviewRequest review) {
        return ResponseEntity.ok(reviewService.updateReviewbyAdmin(reviewId, review.getComment(), review.getRating()));
    }

    //Get review by id
    @GetMapping("/review/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<Review> getReviewById(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    //Get reviews by venue id
    @GetMapping("/review/venue/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<List<Review>> getReviewsByVenue(@PathVariable Integer venueId) {
        return ResponseEntity.ok(reviewService.getReviewsByVenue(venueId));
    }

    //  All user payments
    @GetMapping("/payments/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllUserPayments(@PathVariable Integer userId) {
        return ResponseEntity.ok(paymentService.getAllUserPayments(userId));
    }

    //  All vendor payments
    @GetMapping("/payments/vendors/{vendorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllVendorPayments(@PathVariable Integer  vendorId) {
        return ResponseEntity.ok(paymentService.getAllVendorPayments(vendorId));
    }

    //  All venue payments
    @GetMapping("/payments/venues/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getAllVenuePayments(@PathVariable Integer venueId) {
        return ResponseEntity.ok(paymentService.getAllVenuePayments(venueId));
    }

    // Get all bookings
    @GetMapping("/bookings/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Cancel booking
    @PutMapping("/bookings/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cancelBooking(@PathVariable Integer id) {
        bookingService.adminCancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

}
