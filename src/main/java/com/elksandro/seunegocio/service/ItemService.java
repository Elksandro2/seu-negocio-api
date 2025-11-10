package com.elksandro.seunegocio.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elksandro.seunegocio.dto.business.BusinessSummaryResponse;
import com.elksandro.seunegocio.dto.item.ItemRequest;
import com.elksandro.seunegocio.dto.item.ItemResponse;
import com.elksandro.seunegocio.model.Business;
import com.elksandro.seunegocio.model.Item;
import com.elksandro.seunegocio.repository.BusinessRepository;
import com.elksandro.seunegocio.repository.ItemRepository;
import com.elksandro.seunegocio.service.exception.BusinessNotFoundException;
import com.elksandro.seunegocio.service.exception.ItemNotFoundException;
import com.elksandro.seunegocio.service.exception.UnauthorizedException;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final BusinessRepository businessRepository;
    private final MinioService minioService;

    public ItemService(ItemRepository itemRepository, BusinessRepository businessRepository,
            MinioService minioService) {
        this.itemRepository = itemRepository;
        this.businessRepository = businessRepository;
        this.minioService = minioService;
    }

    public ItemResponse createItem(ItemRequest itemRequest, MultipartFile image, Long loggedUserId) throws Exception {
        validateItemRequest(itemRequest, image);
        
        Business business = businessRepository.findByIdAndOwnerId(itemRequest.businessId(), loggedUserId)
                .orElseThrow(() -> new BusinessNotFoundException(
                        "Negócio não encontrado ou não pertence ao usuário logado."));

        String imageKey = null;
        if (image != null && !image.isEmpty()) {
            imageKey = minioService.uploadFile(image);
        }

        Item item = new Item();
        item.setName(itemRequest.name());
        item.setDescription(itemRequest.description());
        item.setPrice(itemRequest.price());
        item.setOfferType(itemRequest.offerType());
        item.setBusiness(business);
        item.setImageKey(imageKey);

        Item savedItem = itemRepository.save(item);
        return convertToResponse(savedItem);
    }

    public ItemResponse findItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado com ID: " + id));

        return convertToResponse(item);
    }

    public List<ItemResponse> findAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ItemResponse updateItem(Long itemId, ItemRequest itemRequest, Long loggedUserId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado para atualização."));

        verifyItemOwner(item, loggedUserId);

        item.setName(itemRequest.name());
        item.setDescription(itemRequest.description());
        item.setPrice(itemRequest.price());
        item.setOfferType(itemRequest.offerType());

        Item updatedItem = itemRepository.save(item);
        return convertToResponse(updatedItem);
    }

    public void deleteItem(Long itemId, Long loggedUserId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado para remoção."));

        verifyItemOwner(item, loggedUserId);

        if (item.getImageKey() != null) {
            minioService.deleteObject(item.getImageKey());
        }

        itemRepository.delete(item);
    }

    public ItemResponse convertToResponse(Item item) {
        Business business = item.getBusiness();

        BusinessSummaryResponse businessSummary = new BusinessSummaryResponse(
                business.getId(),
                business.getName(),
                business.getAddress(),
                business.getCategoryType().name());

        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getOfferType(),
                minioService.getObjectUrl(item.getImageKey()),
                businessSummary);
    }

    private void verifyItemOwner(Item item, Long loggedUserId) {
        if (!item.getBusiness().getOwner().getId().equals(loggedUserId)) {
            throw new UnauthorizedException("Você não tem permissão para realizar esta operação neste item.");
        }
    }

    private void validateItemRequest(ItemRequest request, MultipartFile image) {
        if (Objects.isNull(request.name()) || request.name().isBlank()) {
            throw new IllegalArgumentException("O nome do item não pode ser vazio.");
        }
        if (request.name().length() > 100) {
            throw new IllegalArgumentException("O nome do item deve ter no máximo 100 caracteres.");
        }

        if (Objects.isNull(request.description()) || request.description().isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }

        if (Objects.isNull(request.price())) {
            throw new IllegalArgumentException("O preço é obrigatório.");
        }

        if (Objects.isNull(request.businessId())) {
            throw new IllegalArgumentException("O ID do Negócio é obrigatório.");
        }

        if (Objects.isNull(request.offerType())) {
            throw new IllegalArgumentException("O tipo do item é obrigatório (PRODUCT ou SERVICE).");
        }

        if (Objects.isNull(image) || image.isEmpty() || image.getSize() == 0) {
            throw new IllegalArgumentException("A imagem do item não pode ser vazia.");
        }
    }

}
