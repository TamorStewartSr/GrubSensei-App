package com.springboot.reviewApp.repository;

import com.springboot.reviewApp.model.ReviewUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ReviewUserRepository extends CrudRepository<ReviewUser, Long> {
    Optional<ReviewUser> findUserByDisplayName(String displayName);
    Optional<ReviewUser> findByDisplayNameAndPassword(String displayName, String password);
}
