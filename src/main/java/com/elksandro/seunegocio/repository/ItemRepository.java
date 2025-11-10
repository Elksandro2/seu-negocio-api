package com.elksandro.seunegocio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elksandro.seunegocio.model.Item;
import com.elksandro.seunegocio.model.enums.CategoryType;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryType(CategoryType categoryType);
}
