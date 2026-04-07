package com.spring.repo;

import com.spring.model.Slot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepo extends JpaRepository<Slot,Integer> {
        List<Slot> findByVenue_VenueIdAndDate(Integer venueId, LocalDate date);
        List<Slot> findByVenue_VenueId(Integer venueId);
    boolean existsByVenue_VenueIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer venueId, LocalDate date, LocalTime end, LocalTime start);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Slot s WHERE s.id = :id")
    Optional<Slot> findByIdForUpdate(@Param("id")Integer id);
}

