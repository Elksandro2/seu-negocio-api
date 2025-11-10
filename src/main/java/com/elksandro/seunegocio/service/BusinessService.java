package com.elksandro.seunegocio.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.elksandro.seunegocio.dto.business.BusinessRequest;
import com.elksandro.seunegocio.dto.business.BusinessResponse;
import com.elksandro.seunegocio.dto.item.ItemResponse;
import com.elksandro.seunegocio.dto.user.UserSummaryResponse;
import com.elksandro.seunegocio.model.Business;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.model.enums.CategoryType;
import com.elksandro.seunegocio.model.enums.Role;
import com.elksandro.seunegocio.repository.BusinessRepository;
import com.elksandro.seunegocio.repository.UserRepository;
import com.elksandro.seunegocio.service.exception.UnauthorizedException;
import com.elksandro.seunegocio.service.exception.UserNotFoundException;

@Service
public class BusinessService {
    
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    public BusinessService(BusinessRepository businessRepository, UserRepository userRepository, ItemService itemService) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    public BusinessResponse createBusiness(BusinessRequest businessRequest, Long ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new UserNotFoundException("Usuário proprietário não encontrado."));

        if (owner.getRole() == Role.BUYER) {
            owner.setRole(Role.SELLER);
            userRepository.save(owner);
        }

        Business business = new Business();
        business.setName(businessRequest.name());
        business.setDescription(businessRequest.description());
        business.setAddress(businessRequest.address());
        business.setCategoryType(businessRequest.categoryType());
        business.setOwner(owner);

        Business savedBusiness = businessRepository.save(business);
        return convertToResponse(savedBusiness);
    }

    public BusinessResponse findBusinessById(Long id) {
        Business business = businessRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("Negócio não encontrado."));
        
        return convertToResponse(business);
    }

    public List<BusinessResponse> findBusinessByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new UserNotFoundException("Usuário proprietário não encontrado."));

        List<Business> businesses = businessRepository.findByOwner(owner);

        return businesses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<BusinessResponse> findBusinessByCategory(CategoryType categoryType) {
        List<Business> businesses = businessRepository.findByCategoryType(categoryType);

        return businesses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public BusinessResponse updateBusiness(Long businessId, BusinessRequest businessRequest, Long loggedUserId) {
        Business business = businessRepository.findByIdAndOwnerId(businessId, loggedUserId)
            .orElseThrow(() -> new UnauthorizedException("Negócio não encontrado ou você não é o proprietário."));
        
        business.setName(businessRequest.name());
        business.setDescription(businessRequest.description());
        business.setAddress(businessRequest.address());
        business.setCategoryType(businessRequest.categoryType());
        
        Business updatedBusiness = businessRepository.save(business);
        return convertToResponse(updatedBusiness);
    }

    public void removeBusiness(Long businessId, Long loggedUserId) {
        Business business = businessRepository.findByIdAndOwnerId(businessId, loggedUserId)
            .orElseThrow(() -> new UnauthorizedException("Negócio não encontrado ou você não tem permissão para remover."));

        businessRepository.delete(business);
    }

    private BusinessResponse convertToResponse(Business business) {
        UserSummaryResponse ownerSummary = new UserSummaryResponse(
            business.getOwner().getId(),
            business.getOwner().getName(),
            business.getOwner().getWhatsapp()
        );

        List<ItemResponse> itemResponses = business.getItems().stream()
                .map(itemService::convertToResponse)
                .collect(Collectors.toList());

        return new BusinessResponse(
            business.getId(),
            business.getName(),
            business.getDescription(),
            business.getAddress(),
            business.getCategoryType().name(),
            business.getCategoryType().getDisplayName(),
            ownerSummary,
            itemResponses
        );
    }
}
