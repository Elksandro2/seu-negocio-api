package com.elksandro.seunegocio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elksandro.seunegocio.model.Business;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.model.enums.CategoryType;


public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByOwnerId(Long ownerId);
    List<Business> findByOwner(User owner);
    Optional<Business> findByIdAndOwnerId(Long businessId, Long ownerId);
    List<Business> findByCategoryType(CategoryType categoryType);
}
