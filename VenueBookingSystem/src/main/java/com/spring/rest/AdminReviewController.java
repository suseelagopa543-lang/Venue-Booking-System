package com.spring.rest;

import com.spring.Request.ReviewRequest;
import com.spring.model.Review;
import com.spring.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/reviews")
public class AdminReviewController
{

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @DeleteMapping("/delete/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }

    @PutMapping("/update/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<Review> updateReview(@PathVariable Integer reviewId, @RequestBody ReviewRequest review) {
        return ResponseEntity.ok(reviewService.updateReviewbyAdmin(reviewId, review.getComment(), review.getRating()));
    }

    @GetMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<Review> getReviewById(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    @GetMapping("/venue/{venueId}")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<List<Review>> getReviewsByVenue(@PathVariable Integer venueId) {
        return ResponseEntity.ok(reviewService.getReviewsByVenue(venueId));
    }


}
