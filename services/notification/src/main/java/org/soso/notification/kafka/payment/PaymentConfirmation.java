package org.soso.notification.kafka.payment;

import org.soso.notification.kafka.payment.PaymentMethod;

import java.math.BigDecimal;

public  record PaymentConfirmation(
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstname,
        String customerLastname,
        String customerEmail
) {
}