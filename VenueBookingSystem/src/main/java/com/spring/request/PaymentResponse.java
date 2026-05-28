package com.spring.request;

import com.spring.model.PaymentMethod;
import com.spring.model.PaymentStatus;
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
