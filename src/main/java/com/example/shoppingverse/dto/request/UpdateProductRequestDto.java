package com.example.shoppingverse.dto.request;

import com.example.shoppingverse.Enum.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDto {

    @NotBlank(message = "Product name cannot be empty")
    private String productName;

    @Min(value = 1,message = "Price must be greater than 0")
    private int price;

    @Min(value = 0,message = "Quantity cannot be negative")
    private int availableQuantity;

    @NotNull(message = "Category is required")
    private ProductCategory category;
}