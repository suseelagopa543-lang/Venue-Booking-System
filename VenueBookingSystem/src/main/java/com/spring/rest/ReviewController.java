package com.spring.rest;

import com.spring.Request.ReviewRequest;
import com.spring.model.Review;
import com.spring.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review>  addReview(@RequestBody ReviewRequest request) {
        Review review = reviewService.addReview(request.getUserId(), request.getVenueId(), request.getComment(), request.getRating());
        return ResponseEntity.ok(review);
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<Review>> getReviewsByVenue(@PathVariable Integer venueId) {
        return ResponseEntity.ok(reviewService.getReviewsByVenue(venueId));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReviewById(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Integer reviewId, @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request.getComment(), request.getRating()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }
}
