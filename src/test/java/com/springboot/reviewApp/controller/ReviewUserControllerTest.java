package com.springboot.reviewApp.controller;

import com.springboot.reviewApp.dto.UserDTO;
import com.springboot.reviewApp.model.ReviewUser;
import com.springboot.reviewApp.repository.ReviewUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class ReviewUserControllerTest {

    @InjectMocks
    private ReviewUserController reviewUserController;

    @Mock
    private ReviewUserRepository reviewUserRepository;

    @Test
    public void testAddReviewUser_Success() {
        ReviewUser user = new ReviewUser();
        user.setDisplayName("testUser");
        user.setPassword("password123");

        when(reviewUserRepository.findUserByDisplayName("testUser")).thenReturn(Optional.empty());

        ResponseEntity<String> response = reviewUserController.addReviewUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully: testUser", response.getBody());
        verify(reviewUserRepository, times(1)).save(user);
    }

    @Test
    public void testAddReviewUser_Conflict() {
        ReviewUser user = new ReviewUser();
        user.setDisplayName("testUser");

        when(reviewUserRepository.findUserByDisplayName("testUser")).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewUserController.addReviewUser(user));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    public void testGetUser_Success() {
        ReviewUser user = new ReviewUser();
        user.setDisplayName("testUser");
        user.setCity("Test City");
        user.setState("Test State");
        user.setZipCode("12345");

        when(reviewUserRepository.findUserByDisplayName("testUser")).thenReturn(Optional.of(user));

        UserDTO userDTO = reviewUserController.getUser("testUser");

        assertNotNull(userDTO);
        assertEquals("testUser", userDTO.displayName());
        assertEquals("Test City", userDTO.city());
        assertEquals("Test State", userDTO.state());
        assertEquals("12345", userDTO.zipCode());
    }

    @Test
    public void testGetUser_NotFound() {
        when(reviewUserRepository.findUserByDisplayName("unknownUser")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewUserController.getUser("unknownUser"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testLogin_Success() {
        ReviewUser user = new ReviewUser();
        user.setDisplayName("testUser");
        user.setPassword("password123");

        when(reviewUserRepository.findByDisplayNameAndPassword("testUser", "password123"))
                .thenReturn(Optional.of(user));

        ReviewUser result = reviewUserController.login(user);

        assertNotNull(result);
        assertEquals("testUser", result.getDisplayName());
        assertNull(result.getPassword()); // Password should be hidden
    }

    @Test
    public void testLogin_InvalidCredentials() {
        ReviewUser user = new ReviewUser();
        user.setDisplayName("testUser");
        user.setPassword("wrongPassword");

        when(reviewUserRepository.findByDisplayNameAndPassword("testUser", "wrongPassword"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewUserController.login(user));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Invalid username or password", exception.getReason());
    }
}
