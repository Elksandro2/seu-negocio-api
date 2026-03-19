package com.elksandro.seunegocio.models.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elksandro.seunegocio.models.review.dto.ReviewRequest;
import com.elksandro.seunegocio.models.review.dto.ReviewResponse;
import com.elksandro.seunegocio.models.review.service.ReviewService;
import com.elksandro.seunegocio.models.user.entity.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody @Valid ReviewRequest request,
            @AuthenticationPrincipal User loggedUser) {
        
        ReviewResponse response = reviewService.createReview(loggedUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/item/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReviewResponse>> getReviewsByItem(@PathVariable Long itemId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByItemId(itemId);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping(value = "/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User loggedUser) {
        
        reviewService.deleteReview(reviewId, loggedUser.getId());
        return ResponseEntity.noContent().build();
    }
}