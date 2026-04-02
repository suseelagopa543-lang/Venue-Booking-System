package com.spring.Request;

import com.spring.model.PaymentMethod;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentRequest {

    private Integer bookingId;
    private Double amount;
    private PaymentMethod paymentMethod;
}
