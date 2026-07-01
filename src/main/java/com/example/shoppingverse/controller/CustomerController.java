package com.example.shoppingverse.controller;

import com.example.shoppingverse.dto.request.CustomerRequestDto;
import com.example.shoppingverse.dto.response.CustomerResponseDto;
import com.example.shoppingverse.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@Tag(name = "Customer APIs", description = "Operations related to customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Operation(
            summary = "Add Customer",
            description = "Creates a new customer and an empty cart"
    )
    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDto> addCustomer(@Valid @RequestBody CustomerRequestDto dto){

        CustomerResponseDto response = customerService.addCustomer(dto);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }


}