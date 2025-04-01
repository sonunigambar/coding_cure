package com.example.codingcure.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayService {
    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // Store fixed course prices (To prevent client-side manipulation)
    private static final Map<String, Integer> COURSE_PRICES = new HashMap<>();

    static {
        COURSE_PRICES.put("Java Programming", 4000);
        COURSE_PRICES.put("Data Structures", 5000);
        COURSE_PRICES.put("Interview Preparation", 2000);
    }

    public String createOrderByCourse(String course) throws RazorpayException {
        if (!COURSE_PRICES.containsKey(course)) {
            throw new IllegalArgumentException("Invalid course selected");
        }

        int amount = COURSE_PRICES.get(course) * 100; // Convert to paise

        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1); // Auto capture payment

        Order order = client.orders.create(orderRequest);
        return order.toString();
    }
}
