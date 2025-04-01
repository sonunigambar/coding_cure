package com.example.codingcure.controller;

import com.example.codingcure.service.RazorpayService;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    private final RazorpayService razorpayService;

    public PaymentController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @GetMapping("/create-order/{course}")
    public ResponseEntity<?> createOrderByCourse(@PathVariable String course) {
        System.out.println("selected course: " + course);
        try {
            String orderResponse = razorpayService.createOrderByCourse(course);
            return ResponseEntity.ok(orderResponse);
        } catch (RazorpayException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> paymentDetails) {
        System.out.println("payment details: " + paymentDetails);
        try {
            String orderId = paymentDetails.get("razorpay_order_id");
            String paymentId = paymentDetails.get("razorpay_payment_id");
            String signature = paymentDetails.get("razorpay_signature");

            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (isValid) {
                return ResponseEntity.ok("Payment verification successful!");
            } else {
                return ResponseEntity.badRequest().body("Payment verification failed!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying payment: " + e.getMessage());
        }
    }
}
