package com.spring.service;

import com.spring.model.Booking;
import com.spring.model.Vendor;
import com.spring.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorService {

    private VendorRepo vendorRepo;

    @Autowired
    public VendorService(VendorRepo vendorRepo) {
        this.vendorRepo = vendorRepo;
    }

    // Get vendor details by ID

    public Vendor getVendorById(Integer id) {
        return vendorRepo.findById(id).orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
    }

    //get all bookings for a vendor
    public List<Booking> getVendorBookings(Integer vendorId) {
        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + vendorId));

        return vendor.getBookings();
    }

}
