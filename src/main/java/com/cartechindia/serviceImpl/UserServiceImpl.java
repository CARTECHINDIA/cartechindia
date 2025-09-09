package com.cartechindia.serviceImpl;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.Role;
import com.cartechindia.entity.User;
import com.cartechindia.exception.InvalidCredentialsException;
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
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;
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

        if (!user.getPassword().equals(loginDetailDto.getPassword())) {
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

        // Check for duplicate email/phone
        if (userRepository.existsByEmail(userDetailDto.getEmail())) {
            return "Email already exists!";
        }

        if (userRepository.existsByPhone(userDetailDto.getPhone())) {
            return "Phone already exists!";
        }

        // Password confirmation
        if (!userDetailDto.getPassword().equals(userDetailDto.getRetypePassword())) {
            throw new RuntimeException("Password and Retype Password do not match!");
        }

        // Map basic fields
        User user = modelMapper.map(userDetailDto, User.class);
        user.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
        user.setActive(false); // default inactive

        // Roles
        Set<Role> roles = userDetailDto.getRole();
        user.setRole(roles);

        // Save user to get ID
        User savedUser = userRepository.saveAndFlush(user);

        // Handle KYC upload if dealer
        if (roles.contains(Role.ROLE_DEALER)) {
            MultipartFile file = userDetailDto.getDocument();
            if (file != null && !file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                if (fileName != null && fileName.matches(".*\\.(pdf|jpg|jpeg|png)$")) {
                    try {
                        Path userDir = Path.of("/opt/app/dealer/kyc", String.valueOf(savedUser.getId()));
                        Files.createDirectories(userDir);

                        Path filePath = userDir.resolve(fileName);
                        Files.write(filePath, file.getBytes());

                        // Save relative path in document field
                        savedUser.setDocument("dealer/kyc/" + savedUser.getId() + "/" + fileName);
                        userRepository.saveAndFlush(savedUser);

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to save KYC document", e);
                    }
                } else {
                    throw new RuntimeException("Only PDF/JPG/JPEG/PNG files allowed for KYC");
                }
            }
        } else {
            // Non-dealer users can be active immediately
            savedUser.setActive(true);
            userRepository.saveAndFlush(savedUser);
        }

        return "User successfully registered!";
    }
}



