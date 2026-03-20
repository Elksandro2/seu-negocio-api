package com.elksandro.seunegocio.models.item.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.elksandro.seunegocio.models.business.dto.BusinessSummaryResponse;
import com.elksandro.seunegocio.models.business.entity.Business;
import com.elksandro.seunegocio.models.business.repository.BusinessRepository;
import com.elksandro.seunegocio.models.business.service.exception.BusinessNotFoundException;
import com.elksandro.seunegocio.models.images.service.MinioService;
import com.elksandro.seunegocio.models.item.dto.ItemRequest;
import com.elksandro.seunegocio.models.item.dto.ItemResponse;
import com.elksandro.seunegocio.models.item.dto.ItemSummaryResponse;
import com.elksandro.seunegocio.models.item.entity.Item;
import com.elksandro.seunegocio.models.item.entity.ItemImage;
import com.elksandro.seunegocio.models.item.repository.ItemRepository;
import com.elksandro.seunegocio.models.item.service.exception.ItemNotFoundException;
import com.elksandro.seunegocio.models.user.service.exception.UnauthorizedException;

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

    public ItemResponse createItem(ItemRequest itemRequest, List<MultipartFile> images, Long loggedUserId) throws Exception {
        validateItemRequest(itemRequest, images);

        Business business = businessRepository.findByIdAndOwnerId(itemRequest.businessId(), loggedUserId)
                .orElseThrow(() -> new BusinessNotFoundException(
                        "Negócio não encontrado ou não pertence ao usuário logado."));

        Item item = new Item();
        item.setName(itemRequest.name());
        item.setDescription(itemRequest.description());
        item.setPrice(itemRequest.price());
        item.setOfferType(itemRequest.offerType());
        item.setStockQuantity(itemRequest.stockQuantity() != null ? itemRequest.stockQuantity() : 0);
        item.setBusiness(business);

        for (MultipartFile imageFile : images) {
            String imageKey = minioService.uploadFile(imageFile);
            ItemImage itemImage = new ItemImage(null, item, imageKey);
            item.getImages().add(itemImage);
        }

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
        item.setStockQuantity(itemRequest.stockQuantity() != null ? itemRequest.stockQuantity() : 0);

        Item updatedItem = itemRepository.save(item);
        return convertToResponse(updatedItem);
    }

    public ItemResponse updateItemStock(Long itemId, Integer newStock, Long loggedUserId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado para atualizar estoque."));

        verifyItemOwner(item, loggedUserId);

        item.setStockQuantity(newStock);
        Item updatedItem = itemRepository.save(item);
        
        return convertToResponse(updatedItem);
    }

    public void deleteItem(Long itemId, Long loggedUserId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado para remoção."));

        verifyItemOwner(item, loggedUserId);

        for (ItemImage img : item.getImages()) {
            minioService.deleteObject(img.getImageKey());
        }

        itemRepository.delete(item);
    }

    public ItemResponse convertToResponse(Item item) {
        Business business = item.getBusiness();

        BusinessSummaryResponse businessSummary = new BusinessSummaryResponse(
                business.getId(), business.getName(), business.getAddress(),
                business.getCategoryType().name(), minioService.getObjectUrl(business.getLogoKey()),
                business.getOwner().getWhatsapp());

        List<String> imageUrls = item.getImages().stream()
                .map(img -> minioService.getObjectUrl(img.getImageKey()))
                .collect(Collectors.toList());

        return new ItemResponse(
                item.getId(), item.getName(), item.getDescription(), item.getPrice(),
                item.getStockQuantity(), item.getOfferType(), 
                imageUrls,
                businessSummary);
    }

    public ItemSummaryResponse convertToSummaryResponse(Item item) {
        String coverImageUrl = item.getImages().isEmpty() ? null : 
                               minioService.getObjectUrl(item.getImages().get(0).getImageKey());

        return new ItemSummaryResponse(
                item.getId(), item.getName(), item.getPrice(), item.getStockQuantity(),
                coverImageUrl,
                item.getOfferType(), item.getBusiness().getName()
        );
    }

    private void verifyItemOwner(Item item, Long loggedUserId) {
        if (!item.getBusiness().getOwner().getId().equals(loggedUserId)) {
            throw new UnauthorizedException("Você não tem permissão para realizar esta operação neste item.");
        }
    }

    private void validateItemRequest(ItemRequest request, List<MultipartFile> images) {
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

        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("Você deve enviar pelo menos uma imagem para o item.");
        }
        
        if (images.size() > 5) {
            throw new IllegalArgumentException("É permitido enviar no máximo 5 imagens por item.");
        }

        for (MultipartFile img : images) {
            if (img.isEmpty() || img.getSize() == 0) {
                throw new IllegalArgumentException("Nenhuma das imagens enviadas pode ser vazia ou corrompida.");
            }
        }
    }

}
