package com.spring.service;

import com.spring.repo.RefreshTokenRepository;
import com.spring.request.RegisterRequest;
import com.spring.model.*;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {


    private UserRepo userRepo;
    private VendorRepo vendorRepo;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private RefreshTokenRepository refreshTokenRepo;

    @Autowired
    public AuthService(UserRepo userRepo, VendorRepo vendorRepo, PasswordEncoder passwordEncoder, UserService userService, RefreshTokenRepository refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
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
                vendor.setVendorStatus(Status.INACTIVE);
                vendor.setApprovalStatus(ApprovalStatus.PENDING);
                vendor.setUser(userSaved);
                vendorRepo.save(vendor);
            }

            return "Registration successfully";
   }

    @Transactional
    public String logout(String refreshToken, Authentication authentication) {

        String email = authentication.getName();

        RefreshToken token = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!token.getUsername().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        refreshTokenRepo.deleteByToken(refreshToken);

        return "Logged Out Successfully";
    }
}
