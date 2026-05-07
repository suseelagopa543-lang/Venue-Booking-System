package com.spring.rest;

import com.spring.Request.RegisterRequest;
import com.spring.Security.JwtService;
import com.spring.model.ApprovalStatus;
import com.spring.model.Role;
import com.spring.model.User;
import com.spring.model.Vendor;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import com.spring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;
    private AuthenticationManager authManager;
    private JwtService jwtService;
    private UserRepo userRepo;
    private VendorRepo vendorRepo;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authManager, JwtService jwtService, UserRepo userRepo, VendorRepo vendorRepo) {
        this.jwtService=jwtService;
         this.authManager = authManager;
        this.authService = authService;
        this.userRepo = userRepo;
        this.vendorRepo = vendorRepo;
     }


     @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request){
        String response= authService.register(request);
        return new ResponseEntity<String>(response, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody RegisterRequest request){
        Authentication auth=authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserEmail(), request.getUserPassword()));

        if(auth.isAuthenticated()){
            User user = userRepo.findByUserEmail(request.getUserEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getRole() == Role.VENDOR) {

                Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                        .orElseThrow(() -> new RuntimeException("Vendor not found"));

                if (vendor.getApprovalStatus() != ApprovalStatus.APPROVED) {

                    return new ResponseEntity<>(
                            "Vendor not approved by admin",
                            HttpStatus.UNAUTHORIZED
                    );
                }
            }

            String jwt=jwtService.generateToken(request.getUserEmail());
            return new ResponseEntity<String>(jwt, HttpStatus.OK);
        }
        else{
            String response="Invalid username or password";
            return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
        }
    }

}
