package com.elksandro.seunegocio.models.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elksandro.seunegocio.models.order.dto.OrderResponse;
import com.elksandro.seunegocio.models.order.service.OrderService;
import com.elksandro.seunegocio.models.user.entity.User;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> checkout(@AuthenticationPrincipal User loggedUser) {
        List<OrderResponse> orders = orderService.checkout(loggedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orders);
    }

    @GetMapping(value = "/customer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> getMyPurchases(@AuthenticationPrincipal User loggedUser) {
        List<OrderResponse> orders = orderService.getCustomerOrders(loggedUser.getId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping(value = "/seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> getMySales(@AuthenticationPrincipal User loggedUser) {
        List<OrderResponse> orders = orderService.getSellerOrders(loggedUser.getId());
        return ResponseEntity.ok(orders);
    }

    // (STANDBY)
    /*
    @PatchMapping(value = "/{orderId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status,
            @AuthenticationPrincipal User loggedUser) {
        OrderResponse response = orderService.updateOrderStatus(orderId, status, loggedUser.getId());
        return ResponseEntity.ok(response);
    }
    */
}