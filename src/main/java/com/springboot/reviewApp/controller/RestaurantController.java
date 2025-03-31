package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.model.Restaurant;
import com.springboot.reviewApp.repository.RestaurantRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RequestMapping("/restaurants")
@RestController
public class RestaurantController {

private final RestaurantRepository restaurantRepository;
private final Pattern zipCodePattern = Pattern.compile("\\d{5}");

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping("/addRestaurant")
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant addRestaurant(@Valid @RequestBody Restaurant restaurant) {
        validateRestaurant(restaurant);

        return restaurantRepository.save(restaurant);
    }

    @GetMapping("/searchByName")
    @ResponseStatus(HttpStatus.OK)
    public List<Restaurant> searchRestaurants(@RequestParam String name) {
        return Optional.of(restaurantRepository.findByNameIgnoreCase(name))
                .filter(restaurants -> !restaurants.isEmpty())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No restaurants found with the name '" + name + "'."));
    }

    @GetMapping("/{id}")
    public Restaurant getRestaurant(@PathVariable Long id) {
        return restaurantRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getRestaurantIdByName")
    public ResponseEntity<Long> getRestaurantIdByName(@RequestParam String name) {
        Restaurant restaurant = restaurantRepository.findByName(name); // Adjust method to match your DB
        if (restaurant != null) {
            return ResponseEntity.ok(restaurant.getId());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public Iterable<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    // Search by zip code and peanut score
    @GetMapping("/searchByZipCodeAndPeanut")
    public List<Restaurant> searchByZipCodeAndPeanut(@RequestParam String zipCode) {
        validateZipCode(zipCode);
        return restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipCode);
    }

    // Search by zip code and dairy score
    @GetMapping("/searchByZipCodeAndDairy")
    public List<Restaurant> searchByZipCodeAndDairy(@RequestParam String zipCode) {
        validateZipCode(zipCode);
        return restaurantRepository.findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(zipCode);
    }

    // Search by zip code and egg score
    @GetMapping("/searchByZipCodeAndEgg")
    public List<Restaurant> searchByZipCodeAndEgg(@RequestParam String zipCode) {
        validateZipCode(zipCode);
        return restaurantRepository.findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(zipCode);
    }

    // Search by name and zipcode
    @GetMapping("/searchByNameAndZipCode")
    public List<Restaurant> searchByNameAndZipCode(@RequestParam String name, @RequestParam String zipCode) {
        validateZipCode(zipCode);
        return restaurantRepository.findRestaurantsByNameAndZipCode(name, zipCode)
                .map(Collections::singletonList) // Convert Optional<Restaurant> to List<Restaurant>
                .orElse(Collections.emptyList()); // Return an empty list if no result
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (ObjectUtils.isEmpty(restaurant.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant name is required.");
        }

        validateZipCode(restaurant.getZipCode());

        Optional<Restaurant> existingRestaurant = restaurantRepository.findRestaurantsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if (existingRestaurant.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,  "A restaurant with this name and zipcode already exists.");
        }
    }

    private void validateZipCode(String zipcode) {
        if (!zipCodePattern.matcher(zipcode).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "Invalid zipcode format.");
        }
    }
}
