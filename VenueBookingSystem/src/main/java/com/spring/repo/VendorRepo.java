package com.spring.repo;

import com.spring.model.Status;
import com.spring.model.User;
import com.spring.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorRepo extends JpaRepository<Vendor,Integer> {


    Optional<Vendor> findByUser_UserId(Integer userId);

    List<Vendor> findByVendorStatus(Status userStatus);



}

