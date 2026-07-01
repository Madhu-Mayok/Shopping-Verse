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
public class ItemRequestDto {

    @NotBlank
    @Email
    String customerEmail;

    @Positive
    int productId;

    @Positive
    int requiredQuantity;
}