package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.model.DiningReview;
import com.springboot.reviewApp.model.Restaurant;
import com.springboot.reviewApp.model.ReviewStatus;
import com.springboot.reviewApp.model.ReviewUser;
import com.springboot.reviewApp.repository.DiningReviewRepository;
import com.springboot.reviewApp.repository.RestaurantRepository;
import com.springboot.reviewApp.repository.ReviewUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiningReviewControllerTest {

    @Mock
    private DiningReviewRepository diningReviewRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ReviewUserRepository reviewUserRepository;

    @InjectMocks
    private DiningReviewController diningReviewController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddDiningReview_Success() {
        // Arrange
        DiningReview review = new DiningReview();
        review.setSubmittedBy("user123");
        review.setRestaurantId(1L);
        review.setPeanutScore(5);
        review.setDairyScore(3);
        review.setEggScore(4);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        ReviewUser reviewUser = new ReviewUser();
        reviewUser.setDisplayName("user123");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reviewUserRepository.findUserByDisplayName("user123")).thenReturn(Optional.of(reviewUser));

        // Act
        assertDoesNotThrow(() -> diningReviewController.addDiningReview(review));

        // Assert
        verify(diningReviewRepository, times(1)).save(review);
    }

    @Test
    public void testAddDiningReview_RestaurantNotFound() {
        // Arrange
        DiningReview review = new DiningReview();
        review.setSubmittedBy("user123");
        review.setRestaurantId(1L);
        review.setPeanutScore(5);
        review.setDairyScore(3);
        review.setEggScore(4);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            diningReviewController.addDiningReview(review);
        });

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatusCode());
    }

    @Test
    public void testAddDiningReview_InvalidUser() {
        // Arrange
        DiningReview review = new DiningReview();
        review.setSubmittedBy("user123");
        review.setRestaurantId(1L);
        review.setPeanutScore(5);
        review.setDairyScore(3);
        review.setEggScore(4);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reviewUserRepository.findUserByDisplayName("user123")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            diningReviewController.addDiningReview(review);
        });

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatusCode());
    }

    @Test
    public void testAddDiningReview_MissingRequiredFields() {
        // Arrange
        DiningReview review = new DiningReview(); // Missing required fields

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            diningReviewController.addDiningReview(review);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    public void testGetReviewsBySubmittedBy() {
        // Arrange
        String submittedBy = "user123";
        DiningReview review = new DiningReview();
        review.setSubmittedBy(submittedBy);

        when(diningReviewRepository.findBySubmittedBy(submittedBy)).thenReturn(List.of(review));

        // Act
        var result = diningReviewController.getReviewsBySubmittedBy(submittedBy);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(submittedBy, result.get(0).getSubmittedBy());
    }

    @Test
    public void testGetReviewsByRestaurantId() {
        // Arrange
        Long restaurantId = 1L;
        DiningReview review = new DiningReview();
        review.setRestaurantId(restaurantId);

        when(diningReviewRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(review));

        // Act
        var result = diningReviewController.getReviewsByRestaurantId(restaurantId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(restaurantId, result.get(0).getRestaurantId());
    }
}
