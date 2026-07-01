package com.example.shoppingverse.controller;

import com.example.shoppingverse.dto.request.CardRequestDto;
import com.example.shoppingverse.dto.response.CardResponseDto;

import com.example.shoppingverse.exception.CustomerNotFoundException;
import com.example.shoppingverse.service.CardService;
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
@RequestMapping("/card")
@Tag(
        name = "Card APIs",
        description = "Customer card operations"
)
public class CardController {

    @Autowired
    CardService cardService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add")
    public ResponseEntity<CardRequestDto> addCard(@Valid @RequestBody CardRequestDto dto){
            CardResponseDto cardResponseDto = cardService.addCard(dto);
            return new ResponseEntity(cardResponseDto, HttpStatus.CREATED);
    }
}