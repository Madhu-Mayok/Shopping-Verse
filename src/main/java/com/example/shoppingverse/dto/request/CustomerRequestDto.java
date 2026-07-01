package com.example.shoppingverse.dto.request;


import com.example.shoppingverse.Enum.Gender;
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
public class CustomerRequestDto {

    @NotBlank(message = "Name cannot be empty")
    String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String emailId;

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile number must contain exactly 10 digits"
    )
    String mobNo;

    @NotBlank(message = "Password should not be empty")
    private String password;

    @NotNull(message = "Gender is required")
    Gender gender;
}