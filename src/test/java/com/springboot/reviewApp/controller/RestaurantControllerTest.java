package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.model.Restaurant;
import com.springboot.reviewApp.repository.RestaurantRepository;
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

public class RestaurantControllerTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantController restaurantController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddRestaurant_Success() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setZipCode("12345");

        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        when(restaurantRepository.findRestaurantsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode()))
                .thenReturn(Optional.empty());

        // Act
        Restaurant result = restaurantController.addRestaurant(restaurant);

        // Assert
        assertNotNull(result);
        assertEquals("Test Restaurant", result.getName());
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    public void testAddRestaurant_NameMissing() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setZipCode("12345");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            restaurantController.addRestaurant(restaurant);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    public void testAddRestaurant_Conflict() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setZipCode("12345");

        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setName("Test Restaurant");
        existingRestaurant.setZipCode("12345");

        when(restaurantRepository.findRestaurantsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode()))
                .thenReturn(Optional.of(existingRestaurant));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            restaurantController.addRestaurant(restaurant);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    public void testGetRestaurant_Success() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        // Act
        Restaurant result = restaurantController.getRestaurant(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Restaurant", result.getName());
    }

    @Test
    public void testGetRestaurant_NotFound() {
        // Arrange
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            restaurantController.getRestaurant(1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testSearchByName_Success() {
        // Arrange
        String name = "Test Restaurant";
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);

        when(restaurantRepository.findByNameIgnoreCase(name)).thenReturn(List.of(restaurant));

        // Act
        var result = restaurantController.searchRestaurants(name);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).getName());
    }

    @Test
    public void testSearchByName_NotFound() {
        // Arrange
        String name = "Nonexistent Restaurant";

        when(restaurantRepository.findByNameIgnoreCase(name)).thenReturn(List.of());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            restaurantController.searchRestaurants(name);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testGetRestaurantIdByName_Success() {
        // Arrange
        String name = "Test Restaurant";
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName(name);

        when(restaurantRepository.findByName(name)).thenReturn(restaurant);

        // Act
        var result = restaurantController.getRestaurantIdByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1L, result.getBody());
    }

    @Test
    public void testGetRestaurantIdByName_NotFound() {
        // Arrange
        String name = "Nonexistent Restaurant";

        when(restaurantRepository.findByName(name)).thenReturn(null);

        // Act
        var result = restaurantController.getRestaurantIdByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testSearchByZipCodeAndPeanut_Success() {
        // Arrange
        String zipCode = "12345";
        Restaurant restaurant = new Restaurant();
        restaurant.setZipCode(zipCode);
        restaurant.setPeanutScore("5");

        when(restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipCode))
                .thenReturn(List.of(restaurant));

        // Act
        var result = restaurantController.searchByZipCodeAndPeanut(zipCode);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(zipCode, result.get(0).getZipCode());
    }

    @Test
    public void testSearchByZipCodeAndPeanut_InvalidZipCode() {
        // Arrange
        String zipCode = "invalidZipCode";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            restaurantController.searchByZipCodeAndPeanut(zipCode);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    public void testValidateZipCode_InvalidFormat() {
        // Arrange
        String zipCode = "invalidZipCode";

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            restaurantController.searchByZipCodeAndPeanut(zipCode);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
