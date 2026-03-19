package com.elksandro.seunegocio.models.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.elksandro.seunegocio.models.images.service.MinioService;
import com.elksandro.seunegocio.models.item.entity.Item;
import com.elksandro.seunegocio.models.item.repository.ItemRepository;
import com.elksandro.seunegocio.models.item.service.exception.ItemNotFoundException;
import com.elksandro.seunegocio.models.review.dto.ReviewRequest;
import com.elksandro.seunegocio.models.review.dto.ReviewResponse;
import com.elksandro.seunegocio.models.review.entity.Review;
import com.elksandro.seunegocio.models.review.repository.ReviewRepository;
import com.elksandro.seunegocio.models.user.entity.User;
import com.elksandro.seunegocio.models.user.repository.UserRepository;
import com.elksandro.seunegocio.models.user.service.exception.UnauthorizedException;
import com.elksandro.seunegocio.models.user.service.exception.UserNotFoundException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final MinioService minioService;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository,
            ItemRepository itemRepository, MinioService minioService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.minioService = minioService;
    }

    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado para avaliação."));

        Review review = new Review();
        review.setUser(user);
        review.setItem(item);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    public List<ReviewResponse> getReviewsByItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException("Item não encontrado.");
        }

        return reviewRepository.findByItemIdOrderByCreatedAtDesc(itemId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteReview(Long reviewId, Long loggedUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada."));

        if (!review.getUser().getId().equals(loggedUserId)) {
            throw new UnauthorizedException("Você só pode apagar as suas próprias avaliações.");
        }

        reviewRepository.delete(review);
    }

    private ReviewResponse convertToResponse(Review review) {
        String profilePicUrl = minioService.getObjectUrl(review.getUser().getProfilePictureKey());

        return new ReviewResponse(
                review.getId(),
                review.getUser().getName(),
                profilePicUrl,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}