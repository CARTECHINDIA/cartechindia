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

        if (!user.getPasswordHash().equals(loginDetailDto.getPassword())) {
            throw new InvalidCredentialsException("Email/Password Invalid!");
        }
        return "Successful Login...";
    }

    @Override
    public String register(UserDetailDto userDetailDto) {

        if (userDetailDto == null) {
            throw new RuntimeException("Invalid User Details...");
        }

        if (userRepository.existsByEmail(userDetailDto.getEmail())) {
            return "Email already exists!";
        }

        if (userRepository.existsByMobileNumber(userDetailDto.getMobileNumber())) {
            return "Phone already exists!";
        }

        // Map basic fields
        User user = modelMapper.map(userDetailDto, User.class);
        user.setPasswordHash(passwordEncoder.encode(userDetailDto.getPasswordHash()));

        // Determine role
        String type = userDetailDto.getType() != null ? userDetailDto.getType().toUpperCase() : "USER";
        Set<Role> roles;
        switch (type) {
            case "DEALER":
                roles = Set.of(Role.ROLE_DEALER);
                user.setActive(false); // inactive until KYC verification
                break;
            case "SELLER":
                roles = Set.of(Role.ROLE_SELLER);
                user.setActive(true);
                break;
            case "BUYER":
            default:
                roles = Set.of(Role.ROLE_USER);
                user.setActive(true);
                break;
        }

        user.setRoles(roles);

        // Save user first to get generated ID
        User savedUser = userRepository.save(user);

        // Optional KYC upload for dealer
        if (roles.contains(Role.ROLE_DEALER)) {
            MultipartFile file = userDetailDto.getKycDocument();

            if (file != null && !file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                if (fileName != null) {
                    String lowerFileName = fileName.toLowerCase();

                    // Validate PDF or image
                    if (!(lowerFileName.endsWith(".pdf") ||
                            lowerFileName.endsWith(".jpg") ||
                            lowerFileName.endsWith(".jpeg") ||
                            lowerFileName.endsWith(".png"))) {
                        throw new RuntimeException("Only PDF or image files (JPG/JPEG/PNG) are allowed");
                    }

                    try {
                        Path uploadPath = Path.of("/opt/app/dealer/kyc/" + savedUser.getId());
                        Files.createDirectories(uploadPath);
                        Path filePath = uploadPath.resolve(fileName);
                        Files.write(filePath, file.getBytes());
                        System.out.println("KYC file saved for user ID: " + savedUser.getId());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to save KYC document", e);
                    }
                }
            } else {
                System.out.println("No KYC document provided at registration (optional)");
            }
        }
        return "User successfully Registered...";
    }
}



