package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentNotification;
import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.kafka.NotificationProducer;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(PaymentRequest request) {
        var payment = paymentRepository.save(mapper.toPayment(request));

        notificationProducer.sendPaymentNotification(
                new PaymentNotification(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstname(),
                        request.customer().lastname(),
                        request.customer().email()

                )
        );

        return payment.getId();
    }
}
