package com.spring.service;

import com.spring.exception.ResourceNotFoundException;
import com.spring.model.BookingStatus;
import com.spring.model.Review;
import com.spring.model.User;
import com.spring.model.Venue;
import com.spring.repo.BookingRepo;
import com.spring.repo.ReviewRepo;
import com.spring.repo.UserRepo;
import com.spring.repo.VenueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private ReviewRepo reviewRepo;
    private VenueRepo venueRepo;
    private UserRepo userRepo;
    private BookingRepo bookingRepo;

    @Autowired
    public ReviewService(ReviewRepo reviewRepo, VenueRepo venueRepo, UserRepo userRepo, BookingRepo bookingRepo) {
        this.reviewRepo = reviewRepo;
        this.venueRepo = venueRepo;
        this.userRepo = userRepo;
        this.bookingRepo=bookingRepo;
    }

    // Add review
    public Review addReview(Integer venueId, String comment, int rating) {

            if (venueId == null) {
                throw new IllegalArgumentException("Venue ID cannot be null");
            }

            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }

            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();

            User user = userRepo.findActiveUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasBooking = bookingRepo
                .existsByUser_UserIdAndVenue_VenueIdAndBookingStatus(
                        user.getUserId(),
                        venueId,
                        BookingStatus.ACTIVE
                );
            if (!hasBooking) {
                throw new RuntimeException("You must book this venue before reviewing");
            }

            Venue venue = venueRepo.findById(venueId)
                    .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        if (reviewRepo.existsByUser_UserIdAndVenue_VenueId(
                user.getUserId(), venueId)) {
            throw new RuntimeException("You already reviewed this venue");
        }
            Review review = new Review();
            review.setUser(user);
            review.setVenue(venue);
            review.setComment(comment);
            review.setRating(rating);
            review.setReviewDate(LocalDateTime.now());

            return reviewRepo.save(review);

    }

    // Get all reviews for a venue
    public List<Review> getReviewsByVenue(Integer venueId) {

        List<Review> reviews = reviewRepo.findByVenue_VenueId(venueId);

        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found for venue id: " + venueId);
        }

        return reviews;
    }

    // Get review by ID
    public Review getReviewById(Integer reviewId) {

        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }

        return reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
    }

    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepo.findAll();

        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found");
        }

        return reviews;
    }

    // Update review
    public Review updateReview(Integer reviewId, String comment, int rating) {

        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepo.findActiveUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not allowed to update this review");
        }
        review.setComment(comment);
        review.setRating(rating);

        return reviewRepo.save(review);
    }

    // Delete review
    public String deleteReview(Integer reviewId) {

        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepo.findActiveUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not allowed to delete this review");
        }
        reviewRepo.delete(review);

        return "Review deleted successfully";
    }

}
