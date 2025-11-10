package com.elksandro.seunegocio.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
    
    public ItemService(ItemRepository itemRepository, BusinessRepository businessRepository) {
        this.itemRepository = itemRepository;
        this.businessRepository = businessRepository;
    }

    public ItemResponse createItem(ItemRequest itemRequest, Long loggedUserId) {
        Business business = businessRepository.findByIdAndOwnerId(itemRequest.businessId(), loggedUserId)
            .orElseThrow(() -> new BusinessNotFoundException("Negócio não encontrado ou não pertence ao usuário logado."));
        
        Item item = new Item();
        item.setName(itemRequest.name());
        item.setDescription(itemRequest.description());
        item.setPrice(itemRequest.price());
        item.setOfferType(itemRequest.offerType());
        item.setBusiness(business);

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
        
        itemRepository.delete(item);
    }

    public ItemResponse convertToResponse(Item item) {
        Business business = item.getBusiness();

        BusinessSummaryResponse businessSummary = new BusinessSummaryResponse(
            business.getId(),
            business.getName(),
            business.getAddress(),
            business.getCategoryType().name()
        );
        
        return new ItemResponse(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getPrice(),
            item.getOfferType(),
            businessSummary 
        );
    }

    private void verifyItemOwner(Item item, Long loggedUserId) {
        if (!item.getBusiness().getOwner().getId().equals(loggedUserId)) {
            throw new UnauthorizedException("Você não tem permissão para realizar esta operação neste item.");
        }
    }
    
}
