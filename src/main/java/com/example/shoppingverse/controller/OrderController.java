package com.example.shoppingverse.controller;

import com.example.shoppingverse.dto.request.OrderRequestDto;
import com.example.shoppingverse.dto.response.OrderResponseDto;
import com.example.shoppingverse.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Tag(
        name = "Order APIs",
        description = "Order placement and management"
)
public class OrderController {

    @Autowired
    OrderService orderService;

    @Operation(
            summary = "Place an order",
            description = "Places an order for a customer using card details"
    )
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/place")
    public ResponseEntity<OrderResponseDto> placeOrder( @Valid @RequestBody OrderRequestDto orderRequestDto){

        OrderResponseDto response =  orderService.placeOrder(orderRequestDto);
        return ResponseEntity  .status(HttpStatus.CREATED) .body(response);
    }
}