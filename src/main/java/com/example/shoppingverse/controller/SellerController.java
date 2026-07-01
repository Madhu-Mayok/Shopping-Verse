package com.example.shoppingverse.controller;

import com.example.shoppingverse.dto.request.SellerRequestDto;
import com.example.shoppingverse.dto.response.SellerResponseDto;
import com.example.shoppingverse.service.SellerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@Tag(
        name = "Seller APIs",
        description = "Seller management operations"
)
public class SellerController {

    @Autowired
    SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<SellerRequestDto> addSeller(@Valid @RequestBody SellerRequestDto dto){

        SellerResponseDto sellerResponseDto = sellerService.addSeller(dto);
        return new ResponseEntity(sellerResponseDto, HttpStatus.CREATED);
    }
}