package com.ecommerce.customer.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CustomerNotFoundException extends RuntimeException {

    private final String message;

    public CustomerNotFoundException(String message) {
        super(message);
        this.message = message;
    }

}
