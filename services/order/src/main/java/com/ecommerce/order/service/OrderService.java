package com.ecommerce.order.service;

import com.ecommerce.order.clients.CustomerClient;
import com.ecommerce.order.clients.ProductClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.exception.BusinessException;
import com.ecommerce.order.kafka.OrderProducer;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;

    private final OrderProducer orderProducer;
    public Integer createOrder(@Valid OrderRequest orderRequest) {

        // 1. Check if customer exists --> customer microservice (OpenFeign)
        var customer = customerClient.findCustomerById(orderRequest.customerId()).orElseThrow(() -> new BusinessException(String.format("Customer not found with id: %s", orderRequest.customerId())));

        // 2. Purchase the products --> product microservice (RestTemplate)
        var purchasedProducts = productClient.purchaseProducts(orderRequest.products());

        // 3. Persist order
        var order = orderRepository.save(orderMapper.toOrder(orderRequest));

        // 4. Persist order lines
        for(PurchaseRequest purchaseRequest : orderRequest.products()){
            orderLineService.saveOrderLine(new OrderLineRequest(
                    null,
                    order.getId(),
                    purchaseRequest.productId(),
                    purchaseRequest.quantity()
            ));
        }

        // 5. Start payment process

        // 6. Send the order confirmation --> notification microservice (kafka)
        orderProducer.sendOrderConfirmation(new OrderConfirmation(
                orderRequest.reference(),
                orderRequest.amount(),
                orderRequest.paymentMethod(),
                customer,
                purchasedProducts
        ));

        return order.getId();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return orderMapper.fromOrder(orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException(String.format("Order not found with id: %d" , orderId))));
    }
}
