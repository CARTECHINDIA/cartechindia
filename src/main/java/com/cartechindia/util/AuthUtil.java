package com.cartechindia.util;

import com.cartechindia.entity.User;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.impl.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

    /**
     * Get the currently authenticated user from SecurityContext
     *
     * @return authenticated User entity
     * @throws NoSuchElementException if no user is authenticated or not found in DB
     */
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new NoSuchElementException("No authenticated user found");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new NoSuchElementException("Authenticated user not found in DB"));
        }

        throw new NoSuchElementException("Authenticated principal is not of type CustomUserDetails");
    }

    /**
     * Get the current user's ID
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Get the current user's full name
     */
    public String getCurrentUserFullName() {
        User user = getCurrentUser();
        return user.getFirstName() + " " + user.getLastName();
    }

    /**
     * Get the current user's email
     */
    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
