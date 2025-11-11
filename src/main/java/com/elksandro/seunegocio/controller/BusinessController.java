package com.elksandro.seunegocio.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elksandro.seunegocio.dto.business.BusinessRequest;
import com.elksandro.seunegocio.dto.business.BusinessResponse;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.model.enums.CategoryType;
import com.elksandro.seunegocio.service.BusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/businesses")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BusinessResponse> createBusiness(
            @RequestPart("businessRequest") String businessRequestJson,
            @RequestPart("logo") MultipartFile logo,
            @AuthenticationPrincipal User loggedUser) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        BusinessRequest businessRequest = objectMapper.readValue(businessRequestJson, BusinessRequest.class);

        BusinessResponse businessResponse = businessService.createBusiness(businessRequest, logo, loggedUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(businessResponse);
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> listCategories() {
        List<String> categories = Arrays.stream(CategoryType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping(value = "/category/{categoryType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BusinessResponse>> getBusinessesByCategory(
            @PathVariable String categoryType) {

        try {
            CategoryType type = CategoryType.valueOf(categoryType.toUpperCase());
            List<BusinessResponse> businesses = businessService.findBusinessByCategory(type);
            return ResponseEntity.ok(businesses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BusinessResponse> getBusinessById(@PathVariable Long id) {
        BusinessResponse businessResponse = businessService.findBusinessById(id);
        return ResponseEntity.ok(businessResponse);
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BusinessResponse>> getMyBusinesses(
            @AuthenticationPrincipal User loggedUser) {

        List<BusinessResponse> businesses = businessService.findBusinessByOwner(loggedUser.getId());
        return ResponseEntity.ok(businesses);
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BusinessResponse> updateBusiness(
            @PathVariable Long id,
            @RequestBody @Valid BusinessRequest businessRequest,
            @AuthenticationPrincipal User loggedUser) {

        BusinessResponse updatedBusiness = businessService.updateBusiness(
                id,
                businessRequest,
                loggedUser.getId());

        return ResponseEntity.ok(updatedBusiness);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBusiness(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser) {

        businessService.removeBusiness(id, loggedUser.getId());

        return ResponseEntity.noContent().build();
    }
}
