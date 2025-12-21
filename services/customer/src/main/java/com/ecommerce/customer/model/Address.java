package com.ecommerce.customer.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Validated
@Builder
public class Address {

    private String street;

    private String houseNumber;

    private String zipCode;

}
