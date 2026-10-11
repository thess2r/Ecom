package com.app.ecom_application.controller;

import com.app.ecom_application.dto.OrderResponse;
import com.app.ecom_application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.hibernate.internal.util.StringHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") String userId){
        return orderService.createOrder(userId)
                .map(orderResponse -> new ResponseEntity<>(orderResponse,HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

}
