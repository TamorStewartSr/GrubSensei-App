package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.model.DiningReview;
import com.springboot.reviewApp.model.Restaurant;
import com.springboot.reviewApp.model.ReviewStatus;
import com.springboot.reviewApp.model.ReviewUser;
import com.springboot.reviewApp.repository.DiningReviewRepository;
import com.springboot.reviewApp.repository.RestaurantRepository;
import com.springboot.reviewApp.repository.ReviewUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequestMapping("/diningReviews")
@RestController
public class DiningReviewController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewUserRepository reviewUserRepository;

    public DiningReviewController(DiningReviewRepository diningReviewRepository, RestaurantRepository restaurantRepository, ReviewUserRepository reviewUserRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.reviewUserRepository = reviewUserRepository;
    }

    @PostMapping("/addReview")
    @ResponseStatus(HttpStatus.CREATED)
    public void addDiningReview(@RequestBody DiningReview diningReview) {
        validateDiningReview(diningReview);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(diningReview.getRestaurantId());
        if (optionalRestaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        diningReview.setReviewStatus(ReviewStatus.PENDING);
        diningReviewRepository.save(diningReview);
    }
    
    @DeleteMapping("/delete/{id}")
    public DiningReview deleteReview(@PathVariable ("id") Long id ) {
        Optional<DiningReview> optionalDiningReview = diningReviewRepository.findById(id);
        if (optionalDiningReview.isEmpty()) {
            return null;
        }
        DiningReview diningReviewDelete = optionalDiningReview.get();
        diningReviewRepository.delete(diningReviewDelete);
        return diningReviewDelete;
    }

    @GetMapping("/reviews/submittedBy/{submittedBy}")
    public List<DiningReview> getReviewsBySubmittedBy(@PathVariable String submittedBy) {
        return diningReviewRepository.findBySubmittedBy(submittedBy);
    }

    @GetMapping("/review/{id}")
    public List<DiningReview> getReviewsByRestaurantId(@PathVariable Long id) {
        return diningReviewRepository.findByRestaurantId(id);
    }

    private void validateDiningReview(DiningReview review) {
        if (ObjectUtils.isEmpty(review.getSubmittedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (ObjectUtils.isEmpty(review.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (ObjectUtils.isEmpty(review.getPeanutScore()) &&
                ObjectUtils.isEmpty(review.getDairyScore()) &&
                ObjectUtils.isEmpty(review.getEggScore())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<ReviewUser> optionalUser = reviewUserRepository.findUserByDisplayName(review.getSubmittedBy());
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PutMapping("/updateReview/id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReviewInfo(@PathVariable Long id, @RequestBody DiningReview updateReview) {
        DiningReview existingReview = diningReviewRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

        copyReviewInfo(updateReview, existingReview);
        diningReviewRepository.save(existingReview);
    }

    private void copyReviewInfo(DiningReview updateReview, DiningReview existingReview) {
        if (updateReview.getComment() != null) {
            existingReview.setComment(updateReview.getComment());
        }

        if (updateReview.getDairyScore() != null) {
            existingReview.setDairyScore(existingReview.getDairyScore());
        }

        if (updateReview.getEggScore() != null) {
            existingReview.setEggScore(existingReview.getEggScore());
        }

        if (updateReview.getPeanutScore() != null) {
            existingReview.setPeanutScore(existingReview.getPeanutScore());
        }
    }

}
