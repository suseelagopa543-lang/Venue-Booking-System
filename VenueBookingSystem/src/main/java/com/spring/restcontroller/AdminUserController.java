package com.spring.restcontroller;

import com.spring.model.User;
import com.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

        @PutMapping("/{userId}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<User> updateUserByAdmin(@PathVariable Integer userId, @RequestBody User updatedUser) {
            User user = userService.updateUserByAdmin(userId,updatedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        String response = userService.deleteUserById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

