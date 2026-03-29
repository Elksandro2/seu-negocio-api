package com.elksandro.seunegocio.models.cartItem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.elksandro.seunegocio.models.cartItem.dto.CartItemRequest;
import com.elksandro.seunegocio.models.cartItem.dto.CartItemResponse;
import com.elksandro.seunegocio.models.cartItem.entity.CartItem;
import com.elksandro.seunegocio.models.cartItem.repository.CartItemRepository;
import com.elksandro.seunegocio.models.item.dto.ItemSummaryResponse;
import com.elksandro.seunegocio.models.item.entity.Item;
import com.elksandro.seunegocio.models.item.repository.ItemRepository;
import com.elksandro.seunegocio.models.item.service.ItemService;
import com.elksandro.seunegocio.models.item.service.exception.ItemNotFoundException;
import com.elksandro.seunegocio.models.user.entity.User;
import com.elksandro.seunegocio.models.user.repository.UserRepository;
import com.elksandro.seunegocio.models.user.service.exception.UserNotFoundException;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public CartItemService(CartItemRepository cartItemRepository, UserRepository userRepository,
            ItemRepository itemRepository, ItemService itemService) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    public List<CartItemResponse> addItemToCart(Long userId, CartItemRequest cartItemRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        Item item = itemRepository.findById(cartItemRequest.itemId())
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado."));

        Optional<CartItem> existingCartItem = cartItemRepository.findByUserIdAndItemId(userId,
                cartItemRequest.itemId());

        CartItem cartItem;

        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequest.quantity());
            cartItem.setScheduledAt(cartItemRequest.scheduledAt());
        } else {
            cartItem = new CartItem(null, user, item, cartItemRequest.quantity(), cartItemRequest.scheduledAt());
        }

        if (cartItem.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItemRepository.save(cartItem);
        }

        return getCartByUserId(userId);
    }

    public List<CartItemResponse> updateQuantity(Long userId, Long itemId, Integer quantity) {
        if (quantity <= 0) {
            return removeItemFromCart(userId, itemId);
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado no carrinho."));
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return getCartByUserId(userId);
    }

    public List<CartItemResponse> getCartByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
             throw new UserNotFoundException("Usuário não encontrado.");
        }
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        return cartItems.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<CartItemResponse> removeItemFromCart(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndItemId(userId, itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado no carrinho."));
        
        cartItemRepository.delete(cartItem);
        
        return getCartByUserId(userId);
    }

    private CartItemResponse convertToResponse(CartItem cartItem) {
        ItemSummaryResponse itemSummary = itemService.convertToSummaryResponse(cartItem.getItem());
        
        BigDecimal subtotal = itemSummary.price().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getQuantity(),
                subtotal,
                itemSummary,
                cartItem.getScheduledAt()
        );
    }
}
