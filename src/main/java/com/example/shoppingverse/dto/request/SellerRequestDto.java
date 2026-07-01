package com.example.shoppingverse.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerRequestDto {

    @NotBlank(message = "Seller name cannot be empty")
    String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String emailId;

    @NotBlank(message = "PAN number cannot be empty")
    String panNo;

    @NotBlank
    String password;
}