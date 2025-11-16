package com.elksandro.seunegocio.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elksandro.seunegocio.dto.business.BusinessRequest;
import com.elksandro.seunegocio.dto.business.BusinessResponse;
import com.elksandro.seunegocio.dto.business.CategoryResponse;
import com.elksandro.seunegocio.dto.item.ItemResponse;
import com.elksandro.seunegocio.dto.user.UserSummaryResponse;
import com.elksandro.seunegocio.model.Business;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.model.enums.CategoryType;
import com.elksandro.seunegocio.model.enums.Role;
import com.elksandro.seunegocio.repository.BusinessRepository;
import com.elksandro.seunegocio.repository.UserRepository;
import com.elksandro.seunegocio.service.exception.BusinessAlreadyExistsException;
import com.elksandro.seunegocio.service.exception.UnauthorizedException;
import com.elksandro.seunegocio.service.exception.UserNotFoundException;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final MinioService minioService;

    public BusinessService(BusinessRepository businessRepository, UserRepository userRepository,
            ItemService itemService, MinioService minioService) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.minioService = minioService;
    }

    public BusinessResponse createBusiness(BusinessRequest businessRequest, MultipartFile logo, Long ownerId)
            throws Exception {
        validateBusinessRequest(businessRequest, logo);

        if (businessRepository.findByName(businessRequest.name()).isPresent()) {
            throw new BusinessAlreadyExistsException(
                    "Já existe um negócio cadastrado com este nome. Tente outro nome.");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Usuário proprietário não encontrado."));

        if (owner.getRole() == Role.BUYER) {
            owner.setRole(Role.SELLER);
            userRepository.save(owner);
        }

        String logoKey = null;
        try {
            logoKey = minioService.uploadFile(logo);

            Business business = new Business();
            business.setName(businessRequest.name());
            business.setDescription(businessRequest.description());
            business.setAddress(businessRequest.address());
            business.setCategoryType(businessRequest.categoryType());
            business.setOwner(owner);
            business.setLogoKey(logoKey);

            Business savedBusiness = businessRepository.save(business);
            return convertToResponse(savedBusiness);
        } catch (Exception e) {
            if (logoKey != null) {
                minioService.deleteObject(logoKey);
            }

            if (e instanceof DataIntegrityViolationException) {
                throw new BusinessAlreadyExistsException(
                        "Falha ao salvar: Nome de negócio já existe ou dados são inválidos.");
            }

            throw e;
        }
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

    public List<CategoryResponse> findAllCategories() {
        return Arrays.stream(CategoryType.values())
                .map(c -> new CategoryResponse(c.name(), c.getDisplayName()))
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
                .orElseThrow(() -> new UnauthorizedException(
                        "Negócio não encontrado ou você não tem permissão para remover."));

        if (business.getLogoKey() != null) {
            minioService.deleteObject(business.getLogoKey());
        }

        businessRepository.delete(business);
    }

    private BusinessResponse convertToResponse(Business business) {
        UserSummaryResponse ownerSummary = new UserSummaryResponse(
                business.getOwner().getId(),
                business.getOwner().getName(),
                business.getOwner().getWhatsapp(),
                minioService.getObjectUrl(business.getLogoKey()),
                business.getOwner().getRole().name());

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
                minioService.getObjectUrl(business.getLogoKey()),
                ownerSummary,
                itemResponses);
    }

    private void validateBusinessRequest(BusinessRequest request, MultipartFile logo) {
        if (Objects.isNull(request.name()) || request.name().isBlank()) {
            throw new IllegalArgumentException("O nome do negócio não pode ser vazio.");
        }

        if (request.name().length() > 100) {
            throw new IllegalArgumentException("O nome do negócio deve ter no máximo 100 caracteres.");
        }

        if (Objects.isNull(request.description()) || request.description().isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }

        if (Objects.isNull(logo) || logo.isEmpty() || logo.getSize() == 0) {
            throw new IllegalArgumentException("A imagem da logo é obrigatória.");
        }

        if (Objects.isNull(request.categoryType())) {
            throw new IllegalArgumentException("A categoria do negócio é obrigatória.");
        }
    }
}
