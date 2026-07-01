package com.example.shoppingverse.service;

import com.example.shoppingverse.dto.response.CartResponseDto;
import com.example.shoppingverse.dto.response.OrderResponseDto;
import com.example.shoppingverse.dto.request.ItemRequestDto;
import com.example.shoppingverse.dto.request.CheckoutCartRequestDto;

import com.example.shoppingverse.exception.*;
import com.example.shoppingverse.model.*;
import com.example.shoppingverse.repository.*;
import com.example.shoppingverse.transformer.CartTransformer;
import com.example.shoppingverse.transformer.OrderTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class CartService {

    private static final Logger log =  LoggerFactory.getLogger(CartService.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CardRespository cardRespository;

    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderService orderService;
    @Autowired
    private OrderEntityRepository orderEntityRepository;

    // customer , product deleted ,product available  and quantity--all valid then this method executes
    // If something doesnt exist is handled in cart service since before reaching this method
    public CartResponseDto addItemToCart(ItemRequestDto itemRequestDto,Item item) {

        Customer customer = customerRepository.findByEmailId(itemRequestDto.getCustomerEmail());
        Product product = productRepository.findById( itemRequestDto.getProductId()).get();

        log.info("product added to cart with Product ID: {}, Active: {}, Quantity: {}",
                product.getId(),
                product.isActive(),
                product.getAvailableQuantity());



        Cart cart = customer.getCart();
        cart.setCartTotal(cart.getCartTotal() + product.getPrice()*itemRequestDto.getRequiredQuantity());

        item.setCart(cart);
        item.setProduct(product);
        Item savedItem = itemRepository.save(item);  // to avoid duplicacy

        cart.getItems().add(savedItem);
        product.getItems().add(savedItem);
        Cart savedCart = cartRepository.save(cart);
        productRepository.save(product);

        //prepare cartResponse Dto
        return CartTransformer.CartToCartResponseDto(savedCart);

    }

    public OrderResponseDto checkoutCart(CheckoutCartRequestDto checkoutCartRequestDto) {

        Customer customer = customerRepository.findByEmailId(checkoutCartRequestDto.getCustomerEmail());
        if(customer==null){
            throw new CustomerNotFoundException("Customer doesn't exist");
        }

        Card card = cardRespository.findByCardNo(checkoutCartRequestDto.getCardNo());
        Date currentDate = new Date();
        if(card==null || card.getCvv()!= checkoutCartRequestDto.getCvv() || currentDate.after(card.getValidTill())){
            throw new InvalidCardException("Card is not valid");
        }

        Cart cart = customer.getCart();
        if(cart == null ||
                cart.getItems().isEmpty()) {
            throw new EmptyCartException("Sorry! The cart is empty");
        }


        OrderEntity order = orderService.placeOrder(cart,card);
        OrderEntity savedOrder = orderEntityRepository.save(order);

        System.out.println("Order Saved : " + savedOrder.getId());

        resetCart(cart);

        // prepare response dto
        return OrderTransformer.OrderToOrderResponseDto(savedOrder);
    }

    public void resetCart(Cart cart){

        cart.setCartTotal(0);
        for(Item item: cart.getItems()){
            item.setCart(null);
        }
        cart.setItems(new ArrayList<>());

    }
}