package com.spring.repo;

import com.spring.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepo extends JpaRepository<Vendor,Integer> {

    Vendor findByUserUserId(Integer userId);
}

