package com.spring.restcontroller;

import com.spring.model.*;
import com.spring.repo.RefreshTokenRepository;
import com.spring.request.AuthResponse;
import com.spring.request.RegisterRequest;
import com.spring.security.JwtService;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import com.spring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;
    private AuthenticationManager authManager;
    private JwtService jwtService;
    private UserRepo userRepo;
    private VendorRepo vendorRepo;
    private RefreshTokenRepository refreshTokenRepo;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authManager, JwtService jwtService, UserRepo userRepo, VendorRepo vendorRepo, RefreshTokenRepository refreshTokenRepo) {
         this.refreshTokenRepo = refreshTokenRepo;
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
    public ResponseEntity<?> loginUser(@RequestBody RegisterRequest request) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserEmail(), request.getUserPassword()));

            if (auth.isAuthenticated()) {
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

                String jwt = jwtService.generateToken(request.getUserEmail());
                String refreshToken = jwtService.generateRefreshToken(request.getUserEmail());
                RefreshToken refresh = new RefreshToken();
                refresh.setToken(refreshToken);
                refresh.setUsername(request.getUserEmail());
                refreshTokenRepo.save(refresh);
                return ResponseEntity.ok(
                        new AuthResponse(
                                jwt,
                                refreshToken));
            }
            String response = "Invalid username or password";
            return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);

        } catch (BadCredentialsException e) {
            String response = "Invalid username or password";
            return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(
            @RequestParam String refreshToken) {

        RefreshToken token =
                refreshTokenRepo
                        .findByToken(refreshToken)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Invalid Refresh Token"));

        String newAccessToken =
                jwtService.generateToken(
                        token.getUsername());

        return new AuthResponse(
                newAccessToken,
                refreshToken);
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String refreshToken,Authentication authentication) {



        return authService.logout(refreshToken,authentication);
    }
}
