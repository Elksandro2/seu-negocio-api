package com.elksandro.seunegocio.models.cartItem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elksandro.seunegocio.models.cartItem.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserIdAndItemId(Long userId, Long itemId);
    List<CartItem> findByUserId(Long userId);
}
