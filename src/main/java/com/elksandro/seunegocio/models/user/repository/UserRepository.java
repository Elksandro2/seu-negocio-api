package com.elksandro.seunegocio.models.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elksandro.seunegocio.models.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
