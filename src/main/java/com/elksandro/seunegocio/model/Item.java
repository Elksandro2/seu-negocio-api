package com.elksandro.seunegocio.model;

import com.elksandro.seunegocio.model.enums.OfferType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
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

    private Double price;

    @Enumerated(EnumType.STRING) 
    @Column(name = "offer_type", nullable = false)
    private OfferType offerType;

    @ManyToOne 
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    private String imageKey;
}
