package com.cartechindia.service.impl;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.Otp;
import com.cartechindia.entity.Role;
import com.cartechindia.entity.User;
import com.cartechindia.exception.AccessDeniedException;
import com.cartechindia.exception.InvalidCredentialsException;
import com.cartechindia.exception.InvalidRoleException;
import com.cartechindia.repository.OtpRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.EmailService;
import com.cartechindia.service.SmsService;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, OtpRepository otpRepository, EmailService emailService, SmsService smsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.smsService = smsService;
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
            throw new InvalidCredentialsException("Invalid User Details");
        }

        // === Duplicate checks ===
        if (userDetailDto.getEmail() != null && userRepository.existsByEmail(userDetailDto.getEmail())) {
            throw new InvalidCredentialsException("Email already exists!");
        }
        if (userDetailDto.getPhone() != null && userRepository.existsByPhone(userDetailDto.getPhone())) {
            throw new InvalidCredentialsException("Phone already exists!");
        }
        if (userDetailDto.getUsername() != null && userRepository.existsByEmail(userDetailDto.getUsername())) {
            throw new InvalidCredentialsException("Username already exists!");
        }

        // === Password confirmation ===
        if (userDetailDto.getPassword() != null &&
                userDetailDto.getRetypePassword() != null &&
                !userDetailDto.getPassword().equals(userDetailDto.getRetypePassword())) {
            throw new InvalidCredentialsException("Password and Retype Password do not match!");
        }

        // === Map DTO to Entity ===
        User user = modelMapper.map(userDetailDto, User.class);
        if (userDetailDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
        }

        // === Role mapping & validation ===
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
        if (userDetailDto.getDob() != null && userDetailDto.getDob().isAfter(LocalDate.now())) {
            throw new InvalidCredentialsException("Date of Birth cannot be in the future!");
        }
        user.setDob(userDetailDto.getDob());

        // === Dealer KYC handling ===
        if (user.getRole() != null && user.getRole().contains(Role.DEALER)) {
            MultipartFile file = userDetailDto.getDocument();
            if (file == null || file.isEmpty()) {
                throw new AccessDeniedException("KYC document is required for DEALER!");
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.matches(".*\\.(pdf|jpg|jpeg|png)$")) {
                throw new AccessDeniedException("Only PDF/JPG/JPEG/PNG allowed for KYC");
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

        // === Save user ===
        user = userRepository.save(user);

        // === OTP generation ===
        String otpCode = String.valueOf(new Random().nextInt(999999)); // 6-digit OTP
        Otp otp = new Otp();
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        otp.setUser(user);
        otp.setEmail(user.getEmail());
        otp.setPhone(user.getPhone());

        otpRepository.save(otp);


        // Call your services
        emailService.sendOtp(user.getEmail(), otpCode);
        smsService.sendOtp(user.getPhone(), otpCode);

        return "OTP sent to email and mobile. Please verify.";


    }

    }
