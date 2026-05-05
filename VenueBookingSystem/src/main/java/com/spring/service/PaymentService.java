package com.spring.service;

import com.spring.Request.PaymentRequest;
import com.spring.Request.PaymentResponse;
import com.spring.Request.VenuePaymentSummary;
import com.spring.exception.ResourceNotFoundException;
import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.repo.PaymentRepo;
import com.spring.repo.SlotRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService{

    private PaymentRepo paymentRepo;
    private BookingRepo bookingRepo;
    private SlotRepo slotRepo;

    @Autowired
    public PaymentService(PaymentRepo paymentRepo,BookingRepo bookingRepo,SlotRepo slotRepo) {
        this.paymentRepo = paymentRepo;
        this.slotRepo=slotRepo;
        this.bookingRepo=bookingRepo;
    }

    @Value("${razorpay.key_id}")
    private String keyId;

    //Make payment
    @Transactional
    public String completePayment(Integer bookingId, String razorpayPaymentId, PaymentMethod paymentMethod) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));


        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Invalid booking state");
        }


        if (booking.getSlots() == null || booking.getSlots().isEmpty()) {
            throw new RuntimeException("No slots found");
        }

        if (paymentRepo.existsByBooking_BookingId(bookingId)) {
            throw new RuntimeException("Payment already completed");
        }

        for (Slot slot : booking.getSlots()) {
            if (slot.getSlotStatus() == SlotStatus.BOOKED) {
                throw new RuntimeException("Slot already booked");
            }
        }


        double totalAmount = booking.getSlots().stream()
                .mapToDouble(slot -> {
                    long hours = Duration.between(slot.getStartTime(), slot.getEndTime()).toHours();
                    return slot.getVenue().getPricePerHour() * hours;
                })
                .sum();

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setAmount(totalAmount);
        payment.setBooking(booking);
        payment.setPaymentMethod(paymentMethod); // or from request
        // Optional: store Razorpay paymentId
        payment.setRazorpayPaymentId(razorpayPaymentId);

        paymentRepo.save(payment);

        for (Slot slot : booking.getSlots()) {
            slot.setSlotStatus(SlotStatus.BOOKED);
        }
        slotRepo.saveAll(booking.getSlots());

        booking.setBookingStatus(BookingStatus.ACTIVE);
        bookingRepo.save(booking);

        return "Payment successful";
    }

    public List<Payment> getUserPayments(Integer userId) {
        return paymentRepo.findByBooking_User_UserId(userId);
    }

    public List<VenuePaymentSummary> getVendorVenuePayments(Integer vendorId) {

        List<Object[]> results = paymentRepo.getVendorRevenueByVenue(vendorId);

        List<VenuePaymentSummary> response = new ArrayList<>();

        for (Object[] row : results) {
            response.add(new VenuePaymentSummary(
                    (Integer) row[0],
                    (String) row[1],
                    (Double) row[2]
            ));
        }

        return response;
    }

    public List<PaymentResponse> getPaymentsByVenue(Integer venueId,Integer vendorId) {

        Booking booking = bookingRepo.findTopByVenue_VenueId(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        Integer actualVendorId = booking.getVenue()
                .getVendor()
                .getVendorId();

        if (!actualVendorId.equals(vendorId)) {
            throw new RuntimeException("Unauthorized: Not your venue");
        }

        List<Payment> payments = paymentRepo.findByBooking_Venue_VenueId(venueId);


        return payments.stream().map(p -> {
            PaymentResponse res = new PaymentResponse();
            res.setPaymentId(p.getPaymentId());
            res.setAmount(p.getAmount());
            res.setMethod(p.getPaymentMethod());
            res.setStatus(p.getPaymentStatus());
            res.setDate(p.getPaymentTime());
            res.setBookingId(p.getBooking().getBookingId());
            return res;
        }).toList();
    }


    public List<Payment> getAllUserPayments() {
        return paymentRepo.findByUserNotNull();
    }

    public List<Payment> getAllVendorPayments() {
        return paymentRepo.findByVendorNotNull();
    }

    public List<Payment> getAllVenuePayments() {
        return paymentRepo.findByVenueNotNull();
    }

}
