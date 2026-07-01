package com.example.shoppingverse.dto.request;


import com.example.shoppingverse.Enum.CardType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardRequestDto {

    @NotBlank
    String customerMobile;

    @NotBlank
    @Pattern(
            regexp = "^[0-9]{16}$",
            message = "Card number must contain 16 digits"
    )
    String cardNo;

    @Min(value = 100,message = "CVV must be 3 digits")
    @Max(value = 999,message = "CVV must be 3 digits")
    int cvv;

    @NotNull
    Date validTill;

    @NotNull
    CardType cardType;
}