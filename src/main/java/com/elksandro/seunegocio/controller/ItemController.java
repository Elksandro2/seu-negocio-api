package com.elksandro.seunegocio.controller;

import java.util.List;

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

import com.elksandro.seunegocio.dto.item.ItemRequest;
import com.elksandro.seunegocio.dto.item.ItemResponse;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.service.ItemService;
import com.elksandro.seunegocio.service.exception.BusinessNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, 
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> createItem(
            @RequestPart("itemRequest") String itemRequestJson,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal User loggedUser) 
            throws Exception { 
        
        ObjectMapper objectMapper = new ObjectMapper();
        ItemRequest itemRequest = objectMapper.readValue(itemRequestJson, ItemRequest.class);

        ItemResponse itemResponse = itemService.createItem(itemRequest, image, loggedUser.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(itemResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = itemService.findAllItems();
        return ResponseEntity.ok(items);
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        ItemResponse item = itemService.findItemById(id);
        return ResponseEntity.ok(item);
    }
    
    @PatchMapping(value = "/{id}", 
                  produces = MediaType.APPLICATION_JSON_VALUE,
                  consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemResponse> updateItem(
        @PathVariable Long id,
        @RequestBody @Valid ItemRequest itemRequest,
        @AuthenticationPrincipal User loggedUser
    ) {
        ItemResponse updatedItem = itemService.updateItem(id, itemRequest, loggedUser.getId());
        return ResponseEntity.ok(updatedItem);
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteItem(
        @PathVariable Long id,
        @AuthenticationPrincipal User loggedUser
    ) {
        itemService.deleteItem(id, loggedUser.getId());
        return ResponseEntity.noContent().build();
    }
}
