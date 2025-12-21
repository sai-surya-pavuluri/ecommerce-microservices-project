package com.ecommerce.customer.dto;

import com.ecommerce.customer.model.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        Address address) {
}
