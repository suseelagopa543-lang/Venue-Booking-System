package com.spring.Request;

import com.spring.model.PaymentMethod;
import com.spring.model.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentResponse {
    private Integer paymentId;
    private Double amount;

    private PaymentMethod method;

    private PaymentStatus status;

    private LocalDateTime date;

    private Integer bookingId;
}
