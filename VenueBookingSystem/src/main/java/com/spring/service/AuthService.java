package com.spring.service;

import com.spring.Request.RegisterRequest;
import com.spring.model.Role;
import com.spring.model.User;
import com.spring.model.Vendor;
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
            user.setUserName(request.getUsername());
            user.setUserEmail(request.getEmail());
            user.setUserPassword(request.getPassword());
            user.setPhoneNumber(request.getPhoneNumber());


            user.setStatus("ACTIVE");
            user.setCreatedAt(LocalDateTime.now());

            if(request.getRole() == Role.VENDOR) {
                user.setRole(Role.VENDOR);
            }
            else {
                user.setRole(Role.USER);
            }
            User userSaved=userService.saveUser(user);
            if(userSaved.getRole() == Role.VENDOR) {
                Vendor vendor = new Vendor();
                vendor.setBusinessName(request.getBusinessName());
                vendor.setBusinessAddress(request.getBusinessAddress());
                vendor.setUser(userSaved);
                vendorRepo.save(vendor);
                }

            return "Registration successfully";
   }
}
