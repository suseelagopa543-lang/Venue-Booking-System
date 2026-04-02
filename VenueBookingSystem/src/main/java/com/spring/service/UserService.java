package com.spring.service;

import com.spring.model.Booking;
import com.spring.model.User;
import com.spring.repo.BookingRepo;
import com.spring.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    public UserRepo userRepo;
    public BookingRepo bookingRepo;


    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, BookingRepo bookingRepo, PasswordEncoder passwordEncoder) {
         this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    // Create a new user with password encoding
    public User saveUser(User user) {
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        return userRepo.save(user);
    }

    // Get user details by ID
    public User getUserDetails(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Update user details (only non-null fields will be updated)
    public User updateUser(Integer userId, User updatedUser) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (updatedUser.getUserName() != null) {
            user.setUserName(updatedUser.getUserName());
        }

        if (updatedUser.getUserEmail() != null) {
            user.setUserEmail(updatedUser.getUserEmail());
        }

        if (updatedUser.getPhoneNumber() != null) {
            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        if (updatedUser.getStatus() != null) {
            user.setStatus(updatedUser.getStatus());
        }

        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }

        // Password (encode before saving)
        if (updatedUser.getUserPassword() != null) {
            user.setUserPassword(passwordEncoder.encode(updatedUser.getUserPassword())); // encode in real app
        }

        return userRepo.save(user);
    }

    // Get all bookings for a user
    public List<Booking> getUserBooking(Integer userId) {
        return bookingRepo.findByUserId(userId);
    }
}
