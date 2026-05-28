package com.spring.service;

import com.spring.request.VendorResponse;
import com.spring.request.VendorUpdateRequest;
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
public class VendorService {

    private VendorRepo vendorRepo;
    private PasswordEncoder passwordEncoder;
    private UserRepo userRepo;
    private BookingRepo bookingRepo;

    @Autowired
    public VendorService(VendorRepo vendorRepo, PasswordEncoder passwordEncoder, UserRepo userRepo, BookingRepo bookingRepo) {
        this.passwordEncoder = passwordEncoder;
        this.vendorRepo = vendorRepo;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    // Get vendor details
    public VendorResponse getVendor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user=userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for user with email: " + username));
        if (vendor.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("Vendor not approved yet");
        }
        return mapVendorResponse(vendor);

    }

    private VendorResponse mapVendorResponse(Vendor vendor) {
        VendorResponse response =new VendorResponse();
        response.setUserName(vendor.getUser().getUserName());
        response.setUserEmail(vendor.getUser().getUserEmail());
        response.setPhoneNumber(vendor.getUser().getPhoneNumber());
        response.setVendorStatus(vendor.getVendorStatus());
        response.setApprovalStatus(vendor.getApprovalStatus());
        response.setBusinessAddress(vendor.getBusinessAddress());
        response.setBusinessName(vendor.getBusinessName());

        return response;
    }

    //get all bookings for a vendor
    public List<Booking> getVendorBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user=userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for user with email: " + username));

        List<Booking> bookings= bookingRepo.findActiveVendorBookings(vendor.getVendorId(), Status.ACTIVE);
        if (bookings.isEmpty()) {
            throw new RuntimeException("No active bookings found for this vendor");
        }
        return bookings;
    }

    @Transactional
    public VendorResponse updateVendor(VendorUpdateRequest updatedVendor) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user=userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for user with email: " + username));

        if(updatedVendor.getBusinessName() != null) {
            vendor.setBusinessName(updatedVendor.getBusinessName());
        }

        if(updatedVendor.getBusinessAddress()!= null) {
            vendor.setBusinessAddress(updatedVendor.getBusinessAddress());
        }

        if(updatedVendor.getUserName()!=null){
            user.setUserName(updatedVendor.getUserName());
        }

        if(updatedVendor.getUserEmail()!=null && !updatedVendor.getUserEmail().equals(user.getUserEmail())){
            if(userRepo.existsByUserEmail(updatedVendor.getUserEmail())){
                throw new RuntimeException("Email already in use by another Vendor.");
            }
            user.setUserEmail(updatedVendor.getUserEmail());
        }

        if(updatedVendor.getPhoneNumber()!=null && !updatedVendor.getPhoneNumber().equals(user.getPhoneNumber())){
            if(userRepo.existsByPhoneNumber(updatedVendor.getPhoneNumber())){
                throw new RuntimeException("Phone number already in use by another Vendor");
            }
            user.setPhoneNumber(updatedVendor.getPhoneNumber());
        }

        if (updatedVendor.getUserPassword() != null && !updatedVendor.getUserPassword().isBlank()) {
            user.setUserPassword(passwordEncoder.encode(updatedVendor.getUserPassword())); // encode in real app
        }

        userRepo.save(user);
        vendorRepo.save(vendor);
        return mapVendorResponse(vendor);
    }


    @Transactional
    public String deactivateVendor(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user=userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));
        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for user with email: " + username));
        if (vendor.getVendorStatus() == Status.INACTIVE) {
            throw new RuntimeException("Vendor already deleted");
        }
        user.setUserStatus(Status.INACTIVE);
        vendor.setVendorStatus(Status.INACTIVE);
        userRepo.save(user);
        vendorRepo.save(vendor);
        return "Vendor deleted Successfully";
    }


    public List<Vendor> getAllVendors() {
        List<Vendor> vendors= vendorRepo.findByVendorStatus(Status.ACTIVE);
        if(vendors.isEmpty()){
            throw new RuntimeException("No active vendors found");
        }
        return vendors;
    }

    public String approveVendor(Integer id) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setVendorStatus(Status.ACTIVE); // or APPROVED
        vendorRepo.save(vendor);

        return "Vendor approved";
    }
    public String rejectVendor(Integer id) {

        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setApprovalStatus(ApprovalStatus.NOTAPPROVED);

        vendor.setVendorStatus(Status.INACTIVE);

        vendorRepo.save(vendor);

        return "Vendor rejected";
    }

    @Transactional
    public String deactivateVendorByAdmin(Integer id) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setVendorStatus(Status.INACTIVE);
        vendorRepo.save(vendor);

        return "Vendor deactivated";
    }

    public List<Booking> getVendorBookingsByAdmin(Integer id) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        List<Booking> bookings= bookingRepo.findActiveVendorBookings(vendor.getVendorId(), Status.ACTIVE);
        if (bookings.isEmpty()) {
            throw new RuntimeException("No active bookings found for this vendor");
        }
        return bookings;
    }

    @Transactional
    public String updateVendorByAdmin(Integer id, Vendor updatedVendor) {
        Vendor vendor = vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if(updatedVendor.getBusinessName() != null) {
            vendor.setBusinessName(updatedVendor.getBusinessName());
        }

        if(updatedVendor.getBusinessAddress()!= null) {
            vendor.setBusinessAddress(updatedVendor.getBusinessAddress());
        }

        vendorRepo.save(vendor);
        return "Vendor updated successfully";
    }

    public Vendor getVendorById(Integer id) {
        return vendorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
    }
}
