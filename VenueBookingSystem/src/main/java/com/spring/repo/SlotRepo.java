package com.spring.repo;

import com.spring.model.Slot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepo extends JpaRepository<Slot,Integer> {
        List<Slot> findByVenueIdAndDate(Integer venueId, LocalDate date);
        List<Slot> findByVenueId(Integer venueId);
    boolean existsByVenueIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer venueId, LocalDate date, LocalTime end, LocalTime start);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Slot> findByIdForUpdate(Integer id);
}

