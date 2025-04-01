package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.model.DiningReview;
import com.springboot.reviewApp.model.Restaurant;
import com.springboot.reviewApp.model.ReviewStatus;
import com.springboot.reviewApp.repository.DiningReviewRepository;
import com.springboot.reviewApp.repository.RestaurantRepository;
import com.springboot.reviewApp.repository.ReviewUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private DiningReviewRepository diningReviewRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ReviewUserRepository reviewUserRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test for Admin Login (Success)
    @Test
    public void testAdminLogin_Success() {
        var response = adminController.adminLogin("admin", "password123");
        assertEquals("Login successful", response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    // ✅ Test for Admin Login (Failure)
    @Test
    public void testAdminLogin_Failure() {
        var response = adminController.adminLogin("wrongUser", "wrongPass");
        assertEquals("Invalid username or password", response.getBody());
        assertEquals(401, response.getStatusCode().value());
    }

    // ✅ Test for Getting Reviews by Status (Valid Status)
    @Test
    public void testGetReviewsByStatus_Valid() {
        DiningReview review = new DiningReview();
        review.setReviewStatus(ReviewStatus.PENDING);
        when(diningReviewRepository.findReviewsByReviewStatus(ReviewStatus.PENDING))
                .thenReturn(List.of(review));

        List<DiningReview> result = adminController.getReviewsByStatus("PENDING");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ReviewStatus.PENDING, result.get(0).getReviewStatus());
    }

    // ✅ Test for Getting Reviews by Status (Invalid Status)
    @Test
    public void testGetReviewsByStatus_Invalid() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminController.getReviewsByStatus("INVALID_STATUS");
        });

        assertTrue(exception.getMessage().contains("Invalid review status"));
    }

    // ✅ Test for Approving a Review
    @Test
    public void testPerformReviewAction_Accepted() {
        DiningReview review = new DiningReview();
        review.setRestaurantId(1L);
        review.setReviewStatus(ReviewStatus.PENDING);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(diningReviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(review);

        // Mock accepted reviews to avoid INTERNAL_SERVER_ERROR
        when(diningReviewRepository.findReviewsByRestaurantIdAndReviewStatus(1L, ReviewStatus.ACCEPTED))
                .thenReturn(List.of(review));

        assertDoesNotThrow(() -> adminController.performReviewAction(1L, Map.of("accepted", true)));
        assertEquals(ReviewStatus.ACCEPTED, review.getReviewStatus());
    }

    // ✅ Test for Rejecting a Review
    @Test
    public void testPerformReviewAction_Rejected() {
        DiningReview review = new DiningReview();
        review.setRestaurantId(1L);
        review.setReviewStatus(ReviewStatus.PENDING);

        when(diningReviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(new Restaurant()));
        when(diningReviewRepository.save(any(DiningReview.class))).thenReturn(review);

        assertDoesNotThrow(() -> adminController.performReviewAction(1L, Map.of("accepted", false)));
        assertEquals(ReviewStatus.REJECTED, review.getReviewStatus());
    }

    // ✅ Test for Review Not Found
    @Test
    public void testPerformReviewAction_ReviewNotFound() {
        when(diningReviewRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminController.performReviewAction(1L, Map.of("accepted", true));
        });

        assertTrue(exception.getMessage().contains("Review not found"));
    }

    // ✅ Test for Missing Accepted Field
    @Test
    public void testPerformReviewAction_MissingAcceptedField() {
        DiningReview review = new DiningReview();
        review.setRestaurantId(1L);
        when(diningReviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(new Restaurant()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminController.performReviewAction(1L, Map.of());
        });

        assertTrue(exception.getMessage().contains("Missing 'accepted' field"));
    }
}
