package com.ecommerce.order.dto;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
        ) {

}
