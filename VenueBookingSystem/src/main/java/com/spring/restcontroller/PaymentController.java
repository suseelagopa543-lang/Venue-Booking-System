package com.spring.restcontroller;

import com.spring.request.PaymentResponse;
import com.spring.request.RazorpayVerifyRequest;
import com.spring.request.VenuePaymentSummary;
import com.spring.model.*;
import com.spring.repo.BookingRepo;
import com.spring.repo.UserRepo;
import com.spring.repo.VendorRepo;
import com.spring.service.PaymentService;
import com.spring.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private PaymentService paymentService;
    private UserRepo userRepo;
    private VendorRepo vendorRepo;
    private BookingRepo bookingRepo;
    private RazorpayService razorpayService;

    @Autowired
    public PaymentController(PaymentService paymentService, UserRepo userRepo,VendorRepo vendorRepo,BookingRepo bookingRepo,RazorpayService razorpayService) {
        this.paymentService = paymentService;
        this.userRepo=userRepo;
        this.vendorRepo=vendorRepo;
        this.bookingRepo=bookingRepo;
        this.razorpayService=razorpayService;
    }

    @Value("${razorpay.key_id}")
    private String keyId;


    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Payment>> userPayments(){
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Payment> payments = paymentService.getUserPayments(user.getUserId());

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/vendors")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<VenuePaymentSummary>> getVendorPayments() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        return ResponseEntity.ok(
                paymentService.getVendorVenuePayments(vendor.getVendorId())
        );
    }


    @GetMapping("/venue/{venueId}/payments")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<List<PaymentResponse>> getVenuePayments(
            @PathVariable Integer venueId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vendor vendor = vendorRepo.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return ResponseEntity.ok(
                paymentService.getPaymentsByVenue(venueId,vendor.getVendorId())
        );
    }

    @PostMapping("/create-order/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public  ResponseEntity<Map<String, Object>>  createOrder(@PathVariable Integer bookingId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepo.findByBookingIdAndUser_UserId(bookingId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Booking not found or not yours"));

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not payable");
        }


        double amount=booking.getSlots().stream()
                        .mapToDouble(slot -> slot.getVenue().getPricePerHour()).sum();


        String orderId = razorpayService.createOrder(bookingId, amount);

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "amount", amount,
                "key", keyId
        ));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> verifyPayment(@RequestBody RazorpayVerifyRequest request){
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepo.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepo
                .findByBookingIdAndUser_UserId(request.getBookingId(), user.getUserId())
                .orElseThrow(() -> new RuntimeException("Booking not found or not yours"));
        if (booking.getBookingStatus() == BookingStatus.ACTIVE) {
            throw new RuntimeException("Payment already completed");
        }

        boolean isValid = razorpayService.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );
       // boolean isValid=true;
        if (!isValid) {
            throw new RuntimeException("Invalid payment signature");
        }

        paymentService.completePayment(
                request.getBookingId(),
                request.getRazorpayPaymentId(),request.getPaymentMethod()
        );

        return ResponseEntity.ok("Payment successful");
    }


}
