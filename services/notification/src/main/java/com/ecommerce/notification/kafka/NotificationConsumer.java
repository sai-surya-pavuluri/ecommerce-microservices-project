package com.ecommerce.notification.kafka;

import com.ecommerce.notification.dto.OrderConfirmation;
import com.ecommerce.notification.dto.PaymentConfirmation;
import com.ecommerce.notification.enums.NotificationType;
import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import com.ecommerce.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "payment-topic", groupId = "paymentGroup")
    public void consumePaymentNotification(PaymentConfirmation paymentConfirmation) {
        log.info(format("Consuming the message from payment-topic payment Topic:: %s:", paymentConfirmation));
        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        try {
            emailService.sendPaymentSuccessEmail(
                    paymentConfirmation.customerEmail(),
                    paymentConfirmation.customerFirstname(),
                    paymentConfirmation.amount(),
                    paymentConfirmation.orderReference()
            );
        }
        catch (Exception e) {
            log.info("Error while sending email to payment-topic payment Topic:: ", e);
        }



    }

    @KafkaListener(topics = "order-topic", groupId = "orderGroup")
    public void consumeOrderNotification(OrderConfirmation orderConfirmation) {
        log.info(format("Consuming the message from order-topic order Topic:: %s:", orderConfirmation));
        notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );

        try{
            emailService.sendOrderConfirmationEmail(
                    orderConfirmation.customer().email(),
                    orderConfirmation.customer().firstname(),
                    orderConfirmation.totalAmount(),
                    orderConfirmation.orderReference(),
                    orderConfirmation.products()
            );
        } catch(Exception e){
            log.info("Error while sending email to order-topic order Topic:: ", e);
        }


    }
}
