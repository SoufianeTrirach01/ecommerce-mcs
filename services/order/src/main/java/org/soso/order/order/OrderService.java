package org.soso.order.order;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.soso.order.customer.CustomerClient;
import org.soso.order.exccption.BusinessException;
import org.soso.order.kafka.OrderConfirmation;
import org.soso.order.kafka.OrderProducer;
import org.soso.order.orderline.OrderLineRequest;
import org.soso.order.orderline.OrderLineService;
import org.soso.order.payment.PaymentClient;
import org.soso.order.payment.PaymentRequest;
import org.soso.order.product.ProductClient;
import org.soso.order.product.PurchaseRequest;
import org.soso.order.product.PurchaseResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;


    public Integer createOrder(OrderRequest request) {

        // check the customer --> OpenFeign
        var customer=customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));
        // purchase the products --> product-ms
        List<PurchaseResponse> purchasedProducts = this.productClient.purchaseProducts(request.products());

        // persist order
        var order =this.repository.save(mapper.toOrder(request));

        // persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
        // start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );
        return   order.getId();

    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
