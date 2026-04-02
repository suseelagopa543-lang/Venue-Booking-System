package com.spring.repo;

import com.spring.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking,Integer>
{
    List<Booking> findByUserId(Integer userId);
}
