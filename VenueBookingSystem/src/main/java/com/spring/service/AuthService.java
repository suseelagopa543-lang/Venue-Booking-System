package com.spring.service;

import com.spring.Request.RegisterRequest;
import com.spring.model.*;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {


    private UserRepo userRepo;
    private VendorRepo vendorRepo;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @Autowired
    public AuthService(UserRepo userRepo, VendorRepo vendorRepo, PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.vendorRepo = vendorRepo;
        this.userService = userService;
    }

    //Register a new user
   public String register(RegisterRequest request){
            User user = new User();
            user.setUserName(request.getUserName());
            user.setUserEmail(request.getUserEmail());
            user.setUserPassword(request.getUserPassword());
            user.setPhoneNumber(request.getPhoneNumber());


            user.setUserStatus(Status.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());

            if(request.getRole() == Role.VENDOR) {
                user.setRole(Role.VENDOR);
            }
            else if(request.getRole() == Role.USER) {
                user.setRole(Role.USER);
            }
            else{
                throw new RuntimeException("Admin registration is not allowed");
            }
            if(request.getRole()==Role.VENDOR) {
                if (request.getBusinessName() == null || request.getBusinessAddress() == null) {
                    throw new RuntimeException("Business details are required for vendor");
                }
            }
            User userSaved=userService.saveUser(user);
            if(userSaved.getRole() == Role.VENDOR) {

                Vendor vendor = new Vendor();
                vendor.setBusinessName(request.getBusinessName());
                vendor.setBusinessAddress(request.getBusinessAddress());
                vendor.setVendorStatus(Status.ACTIVE);
                vendor.setApprovalStatus(ApprovalStatus.APPROVED);
                vendor.setUser(userSaved);
                vendorRepo.save(vendor);
            }

            return "Registration successfully";
   }
}
