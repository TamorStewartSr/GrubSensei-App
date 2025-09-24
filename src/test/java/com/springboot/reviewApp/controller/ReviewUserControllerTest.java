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

    @Test
    public void testVerifyEmail_Success() {
        String token = "valid-token";
        ReviewUser mockUser = new ReviewUser();
        mockUser.setEmailVerificationToken(token);
        mockUser.setEmailVerified(false);

        when(reviewUserRepository.findByEmailVerificationToken(token))
                .thenReturn(Optional.of(mockUser));

        ResponseEntity<String> response = reviewUserController.verifyEmail(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email verified successfully!", response.getBody());
        assertTrue(mockUser.getEmailVerified()); // Should now be true
        assertNull(mockUser.getEmailVerificationToken()); // Should be cleared
        verify(reviewUserRepository, times(1)).save(mockUser);
    }

    @Test
    public void testVerifyEmail_InvalidToken() {
        String token = "invalid-token";

        when(reviewUserRepository.findByEmailVerificationToken(token))
                .thenReturn(Optional.empty());

        ResponseEntity<String> response = reviewUserController.verifyEmail(token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid or expired token", response.getBody());
        verify(reviewUserRepository, never()).save(any(ReviewUser.class));
    }
}
