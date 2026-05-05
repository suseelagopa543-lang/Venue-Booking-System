package com.spring.repo;

import com.spring.model.Slot;
import com.spring.model.SlotStatus;
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
        List<Slot> findByVenue_VenueIdAndDateAndSlotStatusOrderByStartTimeAsc(Integer venueId, LocalDate date,SlotStatus status);
        List<Slot> findByVenue_VenueIdAndSlotStatus(Integer venueId, SlotStatus status);
    boolean existsByVenue_VenueIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer venueId, LocalDate date, LocalTime end, LocalTime start);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Slot s WHERE s.slotId IN :ids")
    List<Slot> findAllByIdForUpdate(@Param("ids") List<Integer> ids);

    boolean existsByVenue_VenueIdAndDateAndStartTimeAndEndTime(
            Integer venueId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );
}

