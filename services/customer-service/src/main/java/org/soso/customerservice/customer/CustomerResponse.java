package org.soso.customerservice.customer;
public record CustomerResponse(
        String id,
        String firstname,
        String lastname,
        String email,
        Address address
) {}