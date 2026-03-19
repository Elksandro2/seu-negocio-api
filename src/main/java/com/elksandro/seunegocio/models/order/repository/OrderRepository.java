package com.elksandro.seunegocio.models.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.elksandro.seunegocio.models.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @Query("SELECT o FROM Order o WHERE o.business.owner.id = :ownerId ORDER BY o.createdAt DESC")
    List<Order> findByBusinessOwnerId(@Param("ownerId") Long ownerId);

    // Verifica se existe algum pedido do cliente que contenha o item específico
    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.customer.id = :customerId AND i.item.id = :itemId")
    boolean existsByCustomerIdAndItemId(@Param("customerId") Long customerId, @Param("itemId") Long itemId);
}