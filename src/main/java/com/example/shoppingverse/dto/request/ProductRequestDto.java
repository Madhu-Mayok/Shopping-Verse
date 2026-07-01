package com.example.shoppingverse.dto.request;


import com.example.shoppingverse.Enum.ProductCategory;
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
public class ProductRequestDto {

    @NotBlank(message = "Seller email is required")
    @Email(message = "Invalid email")
    String sellerEmail;

    @NotBlank(message = "Product name cannot be empty")
    String productName;

    @Positive(message = "Price must be greater than zero")
    int price;

    @Positive(message = "Quantity must be greater than zero")
    int availableQuantity;

    @NotNull(message = "Category is required")
    ProductCategory category;
}