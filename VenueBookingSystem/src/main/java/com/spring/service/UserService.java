package com.spring.service;

import com.spring.exception.ResourceNotFoundException;
import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    public UserRepo userRepo;
    public BookingRepo bookingRepo;
    public VendorRepo vendorRepo;


    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, BookingRepo bookingRepo, PasswordEncoder passwordEncoder, VendorRepo vendorRepo) {
         this.vendorRepo = vendorRepo;
         this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    // Create a new user with password encoding
    public User saveUser(User user) {
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        return userRepo.save(user);
    }

    // Get user details
    public User getUserDetails() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();


        return userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found "));
    }

    // Update user details (only non-null fields will be updated)
    @Transactional
    public User updateUser(User updatedUser) {

        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        String username=auth.getName();


        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found "));

        if (updatedUser.getUserName() != null) {
            user.setUserName(updatedUser.getUserName());
        }

        if (updatedUser.getUserEmail() != null && !updatedUser.getUserEmail().equals(user.getUserEmail())) {
            if(userRepo.existsByUserEmail(updatedUser.getUserEmail())){
                throw new RuntimeException("Email already in use by another user.");
            }
            user.setUserEmail(updatedUser.getUserEmail());
        }

        if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().equals(user.getPhoneNumber())) {
            if(userRepo.existsByPhoneNumber(updatedUser.getPhoneNumber())){
                throw new RuntimeException("Phone number already in use by another user.");
            }
            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        // Password (encode before saving)
        if (updatedUser.getUserPassword() != null && !updatedUser.getUserPassword().isBlank()) {
            user.setUserPassword(passwordEncoder.encode(updatedUser.getUserPassword())); // encode in real app
        }

        return userRepo.save(user);
    }

    // Get all bookings for a user
    public List<Booking> getMyBookings() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepo.findActiveUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Booking> bookings= bookingRepo.findByUser_UserId(user.getUserId());
        if(bookings.isEmpty()){
            throw new ResourceNotFoundException("No bookings found for this user");
        }
        return bookings;

    }

    //delete user account by user
    @Transactional
    public String deleteMyAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // usually email or username

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.VENDOR) {

            Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

             vendor.setVendorStatus(Status.INACTIVE);// delete vendor first
             vendorRepo.save(vendor);
        }

        user.setUserStatus(Status.INACTIVE);
        userRepo.save(user);
        return "User account deleted successfully";
    }

    //delete user account by admin
    @Transactional
    public String deleteUserById(Integer userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() == Role.VENDOR) {

            Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

            vendor.setVendorStatus(Status.INACTIVE); // delete vendor first
            vendorRepo.save(vendor);
        }

        user.setUserStatus(Status.INACTIVE);
        userRepo.save(user);

        return "User deleted successfully";
    }

    public List<User> getAllUsers() {
        List<User> users= userRepo.findByUserStatus(Status.ACTIVE);
        if(users.isEmpty()){
            throw new ResourceNotFoundException("No active users found");
        }
        return users;
    }

    @Transactional
    public User updateUserByAdmin(Integer userId, User updatedUser) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getUserName() != null) {
            user.setUserName(updatedUser.getUserName());
        }

        if (updatedUser.getUserEmail() != null &&
                !updatedUser.getUserEmail().equals(user.getUserEmail())) {

            if (userRepo.existsByUserEmail(updatedUser.getUserEmail())) {
                throw new RuntimeException("Email already exists");
            }

            user.setUserEmail(updatedUser.getUserEmail());
        }

        if (updatedUser.getPhoneNumber() != null &&
                !updatedUser.getPhoneNumber().equals(user.getPhoneNumber())) {

            if (userRepo.existsByPhoneNumber(updatedUser.getPhoneNumber())) {
                throw new RuntimeException("Phone number already exists");
            }

            user.setPhoneNumber(updatedUser.getPhoneNumber());
        }

        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }

        if (updatedUser.getUserStatus() != null) {
            user.setUserStatus(updatedUser.getUserStatus());
        }

        return userRepo.save(user);
    }
}
