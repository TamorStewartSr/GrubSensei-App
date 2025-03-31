package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.model.DiningReview;
import com.springboot.reviewApp.model.Restaurant;
import com.springboot.reviewApp.model.ReviewStatus;
import com.springboot.reviewApp.repository.DiningReviewRepository;
import com.springboot.reviewApp.repository.RestaurantRepository;
import com.springboot.reviewApp.repository.ReviewUserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/admin")
@RestController
public class AdminController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewUserRepository reviewUserRepository;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password123";


    public AdminController(DiningReviewRepository diningReviewRepository, RestaurantRepository restaurantRepository, ReviewUserRepository reviewUserRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.reviewUserRepository = reviewUserRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@Valid @RequestParam String username, @RequestParam String password) {//added @Valid
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/reviews")
    public List<DiningReview> getReviewsByStatus(@RequestParam(value = "review_status", defaultValue = "PENDING") String reviewStatus) {
        try {
            ReviewStatus status = ReviewStatus.valueOf(reviewStatus.toUpperCase());
            return diningReviewRepository.findReviewsByReviewStatus(status);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid review status: " + reviewStatus);
        }
    }

    @PutMapping("/reviews/{reviewId}")
    public void performReviewAction(@PathVariable Long reviewId, @RequestBody Map<String, Boolean> adminReviewAction) {
        Optional<DiningReview> optionalReview = diningReviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }

        DiningReview review = optionalReview.get();
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());

        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Restaurant not found");
        }

        Boolean accepted = adminReviewAction.get("accepted");
        if (accepted == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'accepted' field in the request.");
        }

        if (accepted) {
            review.setReviewStatus(ReviewStatus.ACCEPTED);
            diningReviewRepository.save(review);
            updateRestaurantReviewScores(optionalRestaurant.get());
        } else {
            review.setReviewStatus(ReviewStatus.REJECTED);
            diningReviewRepository.save(review);
        }
    }

    // Test method
    @GetMapping("/reviewStatus/{restaurantId}")
    public List<DiningReview> acceptedReviews(@PathVariable Long restaurantId) {
        return diningReviewRepository.findByRestaurantIdAndReviewStatus(restaurantId, ReviewStatus.ACCEPTED);
    }

    private void updateRestaurantReviewScores(Restaurant restaurant) {
        List<DiningReview> reviews = diningReviewRepository.findReviewsByRestaurantIdAndReviewStatus(restaurant.getId(), ReviewStatus.ACCEPTED);
        if (reviews.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int peanutSum = 0;
        int peanutCount = 0;
        int dairySum = 0;
        int dairyCount = 0;
        int eggSum = 0;
        int eggCount = 0;
        for (DiningReview r : reviews) {
            if (!ObjectUtils.isEmpty(r.getPeanutScore())) {
                peanutSum += r.getPeanutScore();
                peanutCount++;
            }
            if (!ObjectUtils.isEmpty(r.getDairyScore())) {
                dairySum += r.getDairyScore();
                dairyCount++;
            }
            if (!ObjectUtils.isEmpty(r.getEggScore())) {
                eggSum += r.getEggScore();
                eggCount++;
            }
        }

        int totalCount = peanutCount + dairyCount + eggCount ;
        int totalSum = peanutSum + dairySum + eggSum;

        float overallScore = (float) totalSum / totalCount;
        restaurant.setOverallScore(decimalFormat.format(overallScore));

        if (peanutCount > 0) {
            float peanutScore = (float) peanutSum / peanutCount;
            restaurant.setPeanutScore(decimalFormat.format(peanutScore));
        }

        if (dairyCount > 0) {
            float dairyScore = (float) dairySum / dairyCount;
            restaurant.setDairyScore(decimalFormat.format(dairyScore));
        }

        if (eggCount > 0) {
            float eggScore = (float) eggSum / eggCount;
            restaurant.setEggScore(decimalFormat.format(eggScore));
        }

        restaurantRepository.save(restaurant);
    }

}
