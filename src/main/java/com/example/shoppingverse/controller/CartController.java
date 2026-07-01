package com.example.shoppingverse.controller;

import com.example.shoppingverse.dto.response.CartResponseDto;
import com.example.shoppingverse.dto.response.OrderResponseDto;
import com.example.shoppingverse.dto.request.CheckoutCartRequestDto;
import com.example.shoppingverse.dto.request.ItemRequestDto;
import com.example.shoppingverse.model.Item;
import com.example.shoppingverse.repository.CartRepository;
import com.example.shoppingverse.service.CartService;
import com.example.shoppingverse.service.ItemService;
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
@RequestMapping("/cart")
@Tag(
        name = "Cart APIs",
        description = "Shopping cart operations"
)
public class CartController {

    @Autowired
    ItemService itemService;

    @Autowired
    CartService cartService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add")
    public ResponseEntity<ItemRequestDto> addToCart(@Valid @RequestBody ItemRequestDto dto){
            Item item = itemService.createItem(dto);
            CartResponseDto cartResponseDto = cartService.addItemToCart(dto,item);
            return new ResponseEntity(cartResponseDto,HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutCartRequestDto> checkoutCart(@Valid @RequestBody CheckoutCartRequestDto dto){
            OrderResponseDto response = cartService.checkoutCart(dto);
            return new ResponseEntity(response,HttpStatus.CREATED);
    }
}