package com.cartechindia.serviceImpl;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.Role;
import com.cartechindia.entity.User;
import com.cartechindia.exception.InvalidCredentialsException;
import com.cartechindia.exception.InvalidRoleException;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public String login(LoginDetailDto loginDetailDto) {
        User user = userRepository.findByEmail(loginDetailDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email/Password Invalid!"));

        if (!passwordEncoder.matches(loginDetailDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email/Password Invalid!");
        }

        return "Successful Login...";
    }


    @Override
    @Transactional
    public String register(UserDetailDto userDetailDto) {

        if (userDetailDto == null) {
            throw new RuntimeException("Invalid User Details...");
        }

        // === Duplicate checks ===
        if (userDetailDto.getEmail() != null && userRepository.existsByEmail(userDetailDto.getEmail())) {
            return "Email already exists!";
        }
        if (userDetailDto.getPhone() != null && userRepository.existsByPhone(userDetailDto.getPhone())) {
            return "Phone already exists!";
        }
        if (userDetailDto.getUsername() != null && userRepository.existsByEmail(userDetailDto.getUsername())) {
            return "Username already exists!";
        }

        // === Password confirmation ===
        if (userDetailDto.getPassword() != null &&
                userDetailDto.getRetypePassword() != null &&
                !userDetailDto.getPassword().equals(userDetailDto.getRetypePassword())) {
            throw new RuntimeException("Password and Retype Password do not match!");
        }

        // === Map DTO to Entity ===
        User user = modelMapper.map(userDetailDto, User.class);
        if (userDetailDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
        }

        // === Role mapping & validation ===
        Set<Role> roleSet = null;
        if (userDetailDto.getRole() != null && !userDetailDto.getRole().isBlank()) {
            String role = userDetailDto.getRole().trim().toUpperCase();

            // validate against allowed values
            if (!List.of("ADMIN", "SELLER", "DEALER", "BUYER").contains(role)) {
                throw new InvalidRoleException("Invalid role: " + role);
            }

            // wrap single role string into a Set
            user.setRole(Set.of(role));
        }


        // === Active flag ===
        user.setActive(false);

        // === DOB handling ===
        if (userDetailDto.getDob() != null) {
            if (userDetailDto.getDob().isAfter(LocalDate.now())) {
                throw new RuntimeException("Date of Birth cannot be in the future!");
            }
            user.setDob(userDetailDto.getDob());
        }

        // === Dealer KYC handling ===

        if (user.getRole() != null && user.getRole().contains(Role.DEALER)) {
            MultipartFile file = userDetailDto.getDocument();
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("KYC document is required for DEALER!");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.matches(".*\\.(pdf|jpg|jpeg|png)$")) {
                throw new RuntimeException("Only PDF/JPG/JPEG/PNG allowed for KYC");
            }

            try {
                User tempUser = userRepository.saveAndFlush(user);
                Path userDir = Path.of("/opt/app/dealer/kyc", String.valueOf(tempUser.getId()));
                Files.createDirectories(userDir);
                Path filePath = userDir.resolve(fileName);
                Files.write(filePath, file.getBytes());
                tempUser.setDocument("dealer/kyc/" + tempUser.getId() + "/" + fileName);
                user = tempUser;
            } catch (IOException e) {
                throw new RuntimeException("Failed to save KYC document", e);
            }
        }

        userRepository.save(user);
        return "User successfully registered!";
    }

}
