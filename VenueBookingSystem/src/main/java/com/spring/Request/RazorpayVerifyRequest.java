package com.spring.Request;

import com.spring.model.PaymentMethod;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RazorpayVerifyRequest {

        private Integer bookingId;
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private String razorpaySignature;

        private PaymentMethod paymentMethod;

}
