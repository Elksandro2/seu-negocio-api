package com.elksandro.seunegocio.models.business.entity;

import java.util.ArrayList;
import java.util.List;

import com.elksandro.seunegocio.models.business.enums.CategoryType;
import com.elksandro.seunegocio.models.item.entity.Item;
import com.elksandro.seunegocio.models.user.entity.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "businesses")
@Getter
@Setter
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

    private String logoKey;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING) 
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    @OneToMany(mappedBy = "business", cascade = CascadeType.REMOVE)
    private List<Item> items = new ArrayList<>();
}
