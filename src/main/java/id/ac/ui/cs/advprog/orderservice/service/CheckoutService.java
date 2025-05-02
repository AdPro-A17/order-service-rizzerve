package id.ac.ui.cs.advprog.orderservice.service;

import id.ac.ui.cs.advprog.orderservice.dto.CheckoutRequest;
import id.ac.ui.cs.advprog.orderservice.dto.CheckoutResponse;

public interface CheckoutService {
    CheckoutResponse processCheckout(CheckoutRequest request);
}