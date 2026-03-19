package com.elksandro.seunegocio.models.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elksandro.seunegocio.models.business.dto.BusinessResponse;
import com.elksandro.seunegocio.models.business.service.BusinessService;
import com.elksandro.seunegocio.models.user.dto.UserResponse;
import com.elksandro.seunegocio.models.user.service.UserService;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private final UserService userService;
    private final BusinessService businessService;

    public AdminController(UserService userService, BusinessService businessService) {
        this.userService = userService;
        this.businessService = businessService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.removeUserByAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/businesses")
    public ResponseEntity<List<BusinessResponse>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.findAllBusinesses());
    }

    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        businessService.removeBusinessByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}