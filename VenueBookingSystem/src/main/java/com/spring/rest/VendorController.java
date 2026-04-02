package com.spring.rest;

import com.spring.model.Booking;
import com.spring.model.Vendor;
import com.spring.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vendor")
public class VendorController {
    private VendorService vendorService;

    @Autowired
     public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
     }

     @GetMapping("/get-vendorDetails/{vendorId}")
    public ResponseEntity<Vendor> getVendorDetails(@PathVariable Integer vendorId) {
        Vendor vendor = vendorService.getVendorById(vendorId);
        return new ResponseEntity<>(vendor, HttpStatus.OK);
    }

    @GetMapping("/get-vendorBookings/{vendorId}")
    public ResponseEntity<List<Booking>> getVendorBookings(@PathVariable Integer vendorId) {
        List<Booking> list= vendorService.getVendorBookings(vendorId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
