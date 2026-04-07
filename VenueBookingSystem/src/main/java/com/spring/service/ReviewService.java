package com.spring.service;

import com.spring.exception.ResourceNotFoundException;
import com.spring.model.Review;
import com.spring.model.User;
import com.spring.model.Venue;
import com.spring.repo.ReviewRepo;
import com.spring.repo.UserRepo;
import com.spring.repo.VenueRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private ReviewRepo reviewRepo;
    private VenueRepo venueRepo;
    private UserRepo userRepo;

    @Autowired
    public ReviewService(ReviewRepo reviewRepo, VenueRepo venueRepo, UserRepo userRepo) {
        this.reviewRepo = reviewRepo;
        this.venueRepo = venueRepo;
        this.userRepo = userRepo;
    }

    // Add review
    public Review addReview(Integer userId, Integer venueId, String comment, int rating) {

        if (userId == null || venueId == null) {
            throw new IllegalArgumentException("User ID and Venue ID cannot be null");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Venue venue = venueRepo.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + venueId));

        Review review = new Review();
        review.setUser(user);
        review.setVenue(venue);
        review.setComment(comment);
        review.setRating(rating);

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

        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setComment(comment);
        review.setRating(rating);

        return reviewRepo.save(review);
    }

    // Delete review
    public String deleteReview(Integer reviewId) {

        if (reviewId == null) {
            throw new IllegalArgumentException("Review ID cannot be null");
        }

        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        reviewRepo.delete(review);

        return "Review deleted successfully";
    }

}
