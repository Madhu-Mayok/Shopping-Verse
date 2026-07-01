package com.example.shoppingverse.service;

import com.example.shoppingverse.Enum.UserRole;
import com.example.shoppingverse.dto.request.SellerRequestDto;
import com.example.shoppingverse.dto.response.SellerResponseDto;
import com.example.shoppingverse.exception.DuplicateSellerException;
import com.example.shoppingverse.model.Seller;
import com.example.shoppingverse.model.User;
import com.example.shoppingverse.repository.SellerRepository;
import com.example.shoppingverse.repository.UserRepository;
import com.example.shoppingverse.transformer.SellerTransformer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerService {

    @Autowired
    SellerRepository sellerRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public SellerResponseDto addSeller(SellerRequestDto sellerRequestDto) {

        // dto -> entity
        Seller seller = SellerTransformer.SellerRequestDtoToSeller(sellerRequestDto);

        User seller_user = User.builder()
                .email(sellerRequestDto.getEmailId())
                .password(
                        passwordEncoder.encode(
                                sellerRequestDto.getPassword()
                        )
                )
                .role(UserRole.SELLER)
                .build();

        if(sellerRepository.findByEmailId(seller.getEmailId()) != null){
            throw new DuplicateSellerException("Email already exists");
        }

        // save the entity
        Seller savedSeller = sellerRepository.save(seller);

        // save to user
        userRepository.save(seller_user);

        // prepare response dto
        return SellerTransformer.SellerToSellerResponseDto(savedSeller);
    }
}