package com.vikram.ems.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid salary format")
    private BigDecimal salary;

    @PastOrPresent(message = "Hire date cannot be in the future")
    private LocalDate hireDate;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}