package com.spring.restcontroller;

import com.spring.model.Booking;
import com.spring.model.User;
import com.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')") // Only admin can create new users
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser=userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> getUserDetails() {
        User user = userService.getUserDetails();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) {
        User user = userService.updateUser(updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/me/bookings")
    @PreAuthorize("hasAnyRole('USER' or 'ADMIN')")
    public ResponseEntity<List<Booking>> getUserBookings() {
        List<Booking> list= userService.getMyBookings();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteMyAccount() {
        String response = userService.deleteMyAccount();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }





}
