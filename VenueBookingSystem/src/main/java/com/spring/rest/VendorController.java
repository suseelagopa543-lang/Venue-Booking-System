package com.spring.rest;

import com.spring.Request.VendorResponse;
import com.spring.Request.VendorUpdateRequest;
import com.spring.model.Booking;
import com.spring.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendors")
public class VendorController {
    private VendorService vendorService;

    @Autowired
     public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
     }

     //Get Vendor Details
     @GetMapping("/me")
     @PreAuthorize("hasRole('VENDOR")
    public ResponseEntity<VendorResponse> getVendorDetails() {
        VendorResponse response=vendorService.getVendor();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Get Vendor Bookings
    @GetMapping("/me/bookings")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<List<Booking>> getVendorBookings() {
        List<Booking> list= vendorService.getVendorBookings();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    //Update Vendor
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
    public ResponseEntity<VendorResponse> updateVendor(@RequestBody VendorUpdateRequest updatedVendor) {
        VendorResponse response = vendorService.updateVendor(updatedVendor);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Deactivate vendor account
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('VENDOR','ADMIN')")// Only admin can delete any vendor
    public ResponseEntity<String> deleteVendorAccount() {
        String response = vendorService.deactivateVendor();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
