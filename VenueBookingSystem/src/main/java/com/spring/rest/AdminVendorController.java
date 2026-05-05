package com.spring.rest;

import com.spring.model.Booking;
import com.spring.model.Vendor;
import com.spring.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vendors")

public class AdminVendorController {

    @Autowired
    private VendorService vendorService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Vendor> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @PutMapping("/{vendorId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveVendor(@PathVariable Integer vendorId) {
        return vendorService.approveVendor(vendorId);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateVendor(@PathVariable Integer vendorId) {
        return vendorService.deactivateVendorByAdmin(vendorId);
    }

    @GetMapping("/{id}/booking")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getVendorBookings(@PathVariable Integer vendorId) {
        return vendorService.getVendorBookingsByAdmin(vendorId);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateVendor(@PathVariable Integer vendorId, @RequestBody Vendor updatedVendor) {
        return vendorService.updateVendorByAdmin(vendorId, updatedVendor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Vendor getVendorById(@PathVariable Integer id) {
        return vendorService.getVendorById(id);
    }


}
