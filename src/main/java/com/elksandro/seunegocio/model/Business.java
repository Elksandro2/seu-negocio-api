package com.elksandro.seunegocio.model;

import java.util.List;

import com.elksandro.seunegocio.model.enums.CategoryType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "businesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING) 
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    @OneToMany(mappedBy = "business", cascade = CascadeType.REMOVE)
    private List<Item> items;
}
