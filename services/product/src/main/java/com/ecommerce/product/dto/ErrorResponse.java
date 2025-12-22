package com.ecommerce.product.dto;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
