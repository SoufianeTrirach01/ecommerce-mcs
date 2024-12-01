package org.soso.order.kafka;

import org.soso.order.customer.CustomerResponse;
import org.soso.order.order.PaymentMethod;
import org.soso.order.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
){
}
