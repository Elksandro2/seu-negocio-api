package com.elksandro.seunegocio.models.item.entity;

import java.math.BigDecimal;

import com.elksandro.seunegocio.models.business.entity.Business;
import com.elksandro.seunegocio.models.item.enums.OfferType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING) 
    @Column(name = "offer_type", nullable = false)
    private OfferType offerType;

    @ManyToOne 
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    private String imageKey;
}
