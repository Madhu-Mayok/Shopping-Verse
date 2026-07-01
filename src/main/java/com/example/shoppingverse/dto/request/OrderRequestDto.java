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
public class OrderRequestDto {

    @Email
    @NotBlank
    String customerEmail;

    @Positive
    int productId;

    @NotBlank
    String cardNo;

    @Min(100)
    @Max(999)
    int cvv;

    @Positive
    int requiredQuantity;
}