package com.elksandro.seunegocio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.elksandro.seunegocio.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}