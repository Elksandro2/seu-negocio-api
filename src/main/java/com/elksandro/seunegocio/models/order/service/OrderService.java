package com.elksandro.seunegocio.models.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elksandro.seunegocio.models.business.entity.Business;
import com.elksandro.seunegocio.models.cartItem.entity.CartItem;
import com.elksandro.seunegocio.models.cartItem.repository.CartItemRepository;
import com.elksandro.seunegocio.models.images.service.MinioService;
import com.elksandro.seunegocio.models.item.entity.Item;
import com.elksandro.seunegocio.models.item.enums.OfferType;
import com.elksandro.seunegocio.models.item.repository.ItemRepository;
import com.elksandro.seunegocio.models.order.dto.OrderItemResponse;
import com.elksandro.seunegocio.models.order.dto.OrderResponse;
import com.elksandro.seunegocio.models.order.entity.Order;
import com.elksandro.seunegocio.models.order.entity.OrderItem;
import com.elksandro.seunegocio.models.order.enums.OrderStatus;
import com.elksandro.seunegocio.models.order.repository.OrderRepository;
import com.elksandro.seunegocio.models.order.service.exception.InsufficientStockException;
import com.elksandro.seunegocio.models.user.entity.User;
import com.elksandro.seunegocio.models.user.repository.UserRepository;
import com.elksandro.seunegocio.models.user.service.exception.UnauthorizedException;
import com.elksandro.seunegocio.models.user.service.exception.UserNotFoundException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MinioService minioService;

    public OrderService(OrderRepository orderRepository, CartItemRepository cartItemRepository,
            UserRepository userRepository, ItemRepository itemRepository, MinioService minioService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.minioService = minioService;
    }

    @Transactional
    public List<OrderResponse> checkout(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("Cliente não encontrado."));

        List<CartItem> cartItems = cartItemRepository.findByUserId(customerId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Seu carrinho está vazio.");
        }

        Map<Business, List<CartItem>> itemsByBusiness = cartItems.stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getItem().getBusiness()));

        List<Order> generatedOrders = new ArrayList<>();

        for (Map.Entry<Business, List<CartItem>> entry : itemsByBusiness.entrySet()) {
            Business business = entry.getKey();
            List<CartItem> businessCartItems = entry.getValue();

            Order order = new Order();
            order.setCustomer(customer);
            order.setBusiness(business);
            order.setStatus(OrderStatus.PENDING);
            order.setCreatedAt(LocalDateTime.now());
            
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (CartItem cartItem : businessCartItems) {
                Item item = cartItem.getItem();
                
                if (item.getOfferType().equals(OfferType.PRODUCT)) {
                    if (item.getStockQuantity() < cartItem.getQuantity()) {
                        throw new InsufficientStockException("Estoque insuficiente para o produto '" + item.getName() + "'. Disponível: " + item.getStockQuantity());
                    }

                    item.setStockQuantity(item.getStockQuantity() - cartItem.getQuantity());
                    itemRepository.save(item);
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setItem(item);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setScheduledAt(cartItem.getScheduledAt());
                BigDecimal unitPrice = item.getPrice();
                orderItem.setUnitPrice(unitPrice);
                
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                orderItem.setSubtotal(subtotal);

                order.getItems().add(orderItem);
                totalAmount = totalAmount.add(subtotal);
            }

            order.setTotalAmount(totalAmount);
            generatedOrders.add(orderRepository.save(order));
        }

        cartItemRepository.deleteAll(cartItems);

        return generatedOrders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getSellerOrders(Long sellerId) {
        return orderRepository.findByBusinessOwnerId(sellerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, Long loggedUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        boolean isSeller = order.getBusiness().getOwner().getId().equals(loggedUserId);
        boolean isCustomer = order.getCustomer().getId().equals(loggedUserId);

        if (!isSeller && !isCustomer) {
            throw new UnauthorizedException("Você não tem permissão para alterar este pedido.");
        }

        if (isCustomer && !isSeller && newStatus != OrderStatus.CANCELLED) {
             throw new UnauthorizedException("Clientes só podem alterar o status para Cancelado.");
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        return convertToResponse(updatedOrder);
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getCustomer().getName(),
                order.getBusiness().getName(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getStatus().getDescription(),
                order.getCreatedAt(),
                itemResponses
        );
    }

    private OrderItemResponse convertToItemResponse(OrderItem orderItem) {
        Item item = orderItem.getItem();

        String imageUrl = item.getImages().isEmpty() ? null : 
                          minioService.getObjectUrl(item.getImages().get(0).getImageKey());
        
        return new OrderItemResponse(
                orderItem.getId(),
                item.getId(),
                item.getName(),
                imageUrl,
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getSubtotal(),
                item.getOfferType(),
                orderItem.getScheduledAt()
        );
    }
}