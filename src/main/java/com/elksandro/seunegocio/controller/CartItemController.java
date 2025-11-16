package com.elksandro.seunegocio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elksandro.seunegocio.dto.cartItem.CartItemRequest;
import com.elksandro.seunegocio.dto.cartItem.CartItemResponse;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.service.CartItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/cart")
public class CartItemController {
    
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CartItemResponse>> getCart(@AuthenticationPrincipal User loggedUser) {
        
        List<CartItemResponse> cartItems = cartItemService.getCartByUserId(loggedUser.getId());
        
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping(value = "/items", 
                 produces = MediaType.APPLICATION_JSON_VALUE,
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CartItemResponse>> addItemToCart(
            @RequestBody @Valid CartItemRequest cartItemRequest,
            @AuthenticationPrincipal User loggedUser) {

        List<CartItemResponse> updatedCart = cartItemService.addItemToCart(loggedUser.getId(), cartItemRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }
 
    @PatchMapping(value = "/items/{itemId}", 
                  produces = MediaType.APPLICATION_JSON_VALUE,
                  consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CartItemResponse>> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestBody @Valid CartItemRequest cartItemRequest,
            @AuthenticationPrincipal User loggedUser) {

        List<CartItemResponse> updatedCart = cartItemService.updateQuantity(
            loggedUser.getId(),
            itemId,
            cartItemRequest.quantity() 
        );
        
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping(value = "/items/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable Long itemId,
            @AuthenticationPrincipal User loggedUser) {

        cartItemService.removeItemFromCart(loggedUser.getId(), itemId);
        
        return ResponseEntity.noContent().build();
    }
}
