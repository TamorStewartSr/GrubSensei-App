package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.dto.UserDTO;
import com.springboot.reviewApp.model.ReviewUser;
import com.springboot.reviewApp.repository.ReviewUserRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/reviewUsers")
@RestController
public class ReviewUserController {

    private final ReviewUserRepository reviewUserRepository;

    public ReviewUserController(ReviewUserRepository reviewUserRepository) {
        this.reviewUserRepository = reviewUserRepository;
    }

    // Test method Works!!! but test in the frontend
    @PostMapping("/signUp")
    public ResponseEntity<String> addReviewUser(@Valid @RequestBody ReviewUser reviewUser) {
        validateUser(reviewUser);
        reviewUserRepository.save(reviewUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully: " + reviewUser.getDisplayName());
    }

//    @PostMapping("/signUp") //Original method
//    public ResponseEntity<String> addReviewUser(@Valid @RequestBody ReviewUser reviewUser) {
//        try {
//            validateUser(reviewUser); // Validate user details
//            reviewUserRepository.save(reviewUser); // Save the user to the database
//
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body("User registered successfully: " + reviewUser.getDisplayName());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Sign-up failed: " + e.getMessage());
//        }
//    }

    @GetMapping("/{displayName}")
    public UserDTO getUser(@PathVariable String displayName) {
        validateDisplayName(displayName);

        Optional<ReviewUser> existingUser = reviewUserRepository.findUserByDisplayName(displayName);
        if (existingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ReviewUser reviewUser = existingUser.get();

        // Create and return a DTO instead of modifying the entity
        return new UserDTO(
                reviewUser.getDisplayName(),
                reviewUser.getCity(),
                reviewUser.getState(),
                reviewUser.getZipCode()
        );
    }

    @GetMapping
    public Iterable<ReviewUser> getAllUsers() {
        return reviewUserRepository.findAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserInfo(@PathVariable Long id, @RequestBody ReviewUser updatedUser) {
        Optional<ReviewUser> optionalExistingUser = reviewUserRepository.findById(id);
        if (optionalExistingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        ReviewUser existingUser = optionalExistingUser.get();

        copyUserInfoFrom(updatedUser, existingUser);
        reviewUserRepository.save(existingUser);
    }

    private void copyUserInfoFrom(ReviewUser updatedUser, ReviewUser existingUser) {
        if (ObjectUtils.isEmpty(updatedUser.getDisplayName())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        existingUser.setDisplayName(updatedUser.getDisplayName());

        if (!ObjectUtils.isEmpty(updatedUser.getCity())) {
            existingUser.setCity(updatedUser.getCity());
        }

        if (!ObjectUtils.isEmpty(updatedUser.getState())) {
            existingUser.setState(updatedUser.getState());
        }

        if (!ObjectUtils.isEmpty(updatedUser.getZipCode())) {
            existingUser.setZipCode(updatedUser.getZipCode());
        }

        if (!ObjectUtils.isEmpty(updatedUser.getPeanutAllergies())) {
            existingUser.setPeanutAllergies(updatedUser.getPeanutAllergies());
        }

        if (!ObjectUtils.isEmpty(updatedUser.getDairyAllergies())) {
            existingUser.setDairyAllergies(updatedUser.getDairyAllergies());
        }

        if (!ObjectUtils.isEmpty(updatedUser.getEggAllergies())) {
            existingUser.setEggAllergies(updatedUser.getEggAllergies());
        }
    }

    private void validateUser(ReviewUser reviewUser) {
        validateDisplayName(reviewUser.getDisplayName());

        Optional<ReviewUser> existingUser = reviewUserRepository.findUserByDisplayName(reviewUser.getDisplayName());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void validateDisplayName(String displayName) {
        if (ObjectUtils.isEmpty(displayName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Display name cannot be empty.");
        }
    }

    @PostMapping("/login")
    public ReviewUser login(@RequestBody @NonNull ReviewUser loginRequest) {
        Optional<ReviewUser> optionalUser = reviewUserRepository.findByDisplayNameAndPassword(loginRequest.getDisplayName(), loginRequest.getPassword());

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid username or password");
        }

        ReviewUser user = optionalUser.get();
        user.setPassword(null); // Hide password before returning
        return user;
    }

}
