package com.example.shoppingverse.service;

import com.example.shoppingverse.Enum.UserRole;
import com.example.shoppingverse.dto.request.CustomerRequestDto;
import com.example.shoppingverse.dto.response.CustomerResponseDto;
import com.example.shoppingverse.exception.DuplicateCustomerException;
import com.example.shoppingverse.model.Cart;
import com.example.shoppingverse.model.Customer;
import com.example.shoppingverse.model.User;
import com.example.shoppingverse.repository.CustomerRepository;
import com.example.shoppingverse.repository.UserRepository;
import com.example.shoppingverse.transformer.CustomerTransformer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Service
@Transactional
public class CustomerService {

    private static final Logger log =  LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    CustomerRepository customerRepository;  // filed injection

    private final UserRepository userRepository;  // constructor injection

    private final PasswordEncoder passwordEncoder;

    public CustomerResponseDto addCustomer(CustomerRequestDto customerRequestDto) {

        log.info("Adding customer with email: {}", customerRequestDto.getEmailId());

        // dto -> entity
        Customer customer = CustomerTransformer.customerRequestDtoToCustomer(customerRequestDto);

        User user_customer = User.builder()
                .email(customerRequestDto.getEmailId())
                .password( passwordEncoder.encode( customerRequestDto.getPassword() ) )
                .role(UserRole.CUSTOMER)
                .build();

        Cart cart = new Cart();
        cart.setCartTotal(0);
        cart.setCustomer(customer);
        customer.setCart(cart);

        //duplicate mails
        if(customerRepository.findByEmailId(customer.getEmailId()) != null){
            throw new DuplicateCustomerException("Email already exists");
        }

        //duplicate mobile nums
        if(customerRepository.findByMobNo(customer.getMobNo()) != null){
            throw new DuplicateCustomerException("Mobile number already exists");
        }


        Customer savedCustomer = customerRepository.save(customer);   // saves both customer and cart;

        log.info("Customer created successfully with id: {}", savedCustomer.getId());

        userRepository.save(user_customer);  // save user

        // prepare the response dto
        return CustomerTransformer.customerToCustomerResponseDto(savedCustomer);

    }
}