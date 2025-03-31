package com.springboot.reviewApp.repository;

import com.springboot.reviewApp.model.DiningReview;
import com.springboot.reviewApp.model.ReviewStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DiningReviewRepository extends CrudRepository<DiningReview, Long> {
    List<DiningReview> findReviewsByRestaurantIdAndReviewStatus(Long restaurantId, ReviewStatus reviewStatus);
    List<DiningReview> findReviewsByReviewStatus(ReviewStatus reviewStatus);
    List<DiningReview> findByRestaurantId(Long restaurantId);
    List<DiningReview> findByRestaurantIdAndReviewStatus(Long restaurantId, ReviewStatus reviewStatus);

    List<DiningReview> findBySubmittedBy(String submittedBy);
}
