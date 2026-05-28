package com.spring.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@ToString
public class SlotDTO {
    private Integer slotId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String slotStatus;
}
