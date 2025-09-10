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
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (userDetailDto == null) throw new RuntimeException("Invalid User Details...");

        // Check for duplicates
        if (userDetailDto.getEmail() != null && userRepository.existsByEmail(userDetailDto.getEmail()))
            return "Email already exists!";

        if (userDetailDto.getPhone() != null && userRepository.existsByPhone(userDetailDto.getPhone()))
            return "Phone already exists!";

        if (userDetailDto.getUsername() != null && userRepository.existsByEmail(userDetailDto.getEmail()))
            return "Username already exists!";

        // Password confirmation
        if (userDetailDto.getPassword() != null && userDetailDto.getRetypePassword() != null &&
                !userDetailDto.getPassword().equals(userDetailDto.getRetypePassword()))
            throw new RuntimeException("Password and Retype Password do not match!");

        User user = modelMapper.map(userDetailDto, User.class);

        if (userDetailDto.getPassword() != null)
            user.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));

        // Map text roles (comma-separated) to Enum Set<Role>
        Set<Role> roleSet = null;
        if (userDetailDto.getRoles() != null && !userDetailDto.getRoles().isEmpty()) {
            roleSet = Arrays.stream(userDetailDto.getRoles().split(","))
                    .map(String::trim)                  // remove extra spaces
                    .map(r -> {
                        try {
                            return Role.valueOf(r.toUpperCase()); // convert to Enum
                        } catch (IllegalArgumentException e) {
                            return null; // ignore invalid roles
                        }
                    })
                    .filter(r -> r != null)
                    .collect(Collectors.toSet());
        }
        user.setRole(roleSet);



        // Set active flag
        if (!user.isActive())
            user.setActive(user.getRole() == null || !user.getRole().contains(Role.DEALER));

        // Set DOB if provided
        if (userDetailDto.getDob() != null) user.setDob(userDetailDto.getDob());

        // Handle KYC for dealers
        if (user.getRole() != null && user.getRole().contains(Role.DEALER)) {
            MultipartFile file = userDetailDto.getDocument();
            if (file != null && !file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                if (fileName == null || !fileName.matches(".*\\.(pdf|jpg|jpeg|png)$"))
                    throw new RuntimeException("Only PDF/JPG/JPEG/PNG allowed for KYC");

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
        }

        userRepository.save(user);
        return "User successfully registered!";
    }
}
