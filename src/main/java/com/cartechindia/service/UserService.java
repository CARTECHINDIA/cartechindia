package com.cartechindia.service;

import com.cartechindia.dto.request.UserDto;
import com.cartechindia.entity.User;
import com.cartechindia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Logger for this class
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<User> getAll() {
        logger.info("Fetching all users");
        try {
            List<User> users = userRepository.findAll();
            logger.debug("Retrieved {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw e;
        }
    }

    public User getById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                logger.warn("User with ID {} not found", id);
            } else {
                logger.debug("User found: {}", user.getEmail());
            }
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user with ID: {}", id, e);
            throw e;
        }
    }

    public User save(User user) {
        logger.info("Saving user: {}", user.getEmail());
        try {
            User savedUser = userRepository.save(user);
            logger.debug("User saved with ID: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error saving user: {}", user.getEmail(), e);
            throw e;
        }
    }

    public void delete(Long id) {
        logger.info("Deleting user with ID: {}", id);
        try {
            userRepository.deleteById(id);
            logger.debug("User with ID {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            throw e;
        }
    }
}
