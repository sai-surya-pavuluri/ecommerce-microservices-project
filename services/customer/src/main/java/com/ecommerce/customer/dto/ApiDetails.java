package com.ecommerce.customer.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "api")
public record ApiDetails(Map<String,String> metadata, List<String> contact) {
}
