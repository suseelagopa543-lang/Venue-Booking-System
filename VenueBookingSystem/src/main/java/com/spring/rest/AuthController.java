package com.spring.rest;

import com.spring.Request.RegisterRequest;
import com.spring.Security.JwtService;
import com.spring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authManager, JwtService jwtService) {
        this.jwtService=jwtService;
         this.authManager = authManager;
        this.authService = authService;
     }

     @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){
        String response= authService.register(request);
        return new ResponseEntity<String>(response, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody RegisterRequest request){
        Authentication auth=authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if(auth.isAuthenticated()){
            String jwt=jwtService.generateToken(request.getUsername());
            return new ResponseEntity<String>(jwt, HttpStatus.OK);
        }
        else{
            String response="Invalid username or password";
            return new ResponseEntity<String>(response, HttpStatus.UNAUTHORIZED);
        }
    }

}
