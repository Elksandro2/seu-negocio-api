package com.elksandro.seunegocio.models.business.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elksandro.seunegocio.models.business.entity.Business;
import com.elksandro.seunegocio.models.business.enums.CategoryType;
import com.elksandro.seunegocio.models.user.entity.User;


public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByOwnerId(Long ownerId);
    List<Business> findByOwner(User owner);
    Optional<Business> findByIdAndOwnerId(Long businessId, Long ownerId);
    List<Business> findByCategoryType(CategoryType categoryType);
    Optional<Business> findByName(String name);
}
