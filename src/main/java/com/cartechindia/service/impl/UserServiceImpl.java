package com.cartechindia.service.impl;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.entity.Otp;
import com.cartechindia.entity.Role;
import com.cartechindia.entity.User;
import com.cartechindia.entity.UserStatus;
import com.cartechindia.exception.*;
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

    private final Random random = new Random();

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ModelMapper modelMapper,
                           OtpRepository otpRepository,
                           EmailService emailService,
                           SmsService smsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.smsService = smsService;
    }


    @Override
    @Transactional
    public void updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        // Update status
        user.setStatus(status);

        // Only set active = true if status is APPROVED
        if (status == UserStatus.APPROVED) {
            user.setActive(true);
        }

        userRepository.save(user);
    }


    @Override
    public List<User> getUnapprovedUsers() {
        return userRepository.findByStatusNot(UserStatus.APPROVED);
    }


    @Override
    public String login(LoginDetailDto loginDetailDto) {
        // Fetch user by email
        User user = userRepository.findByEmail(loginDetailDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email/Password Invalid!"));

        // Check password
        if (!passwordEncoder.matches(loginDetailDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email/Password Invalid!");
        }

        // Check active flag
        if (!user.isActive()) {
            throw new InactiveUserException("User is inactive. Status: %s".formatted(user.getStatus()));
        }


        // Optional: Still block users who are not APPROVED
        if (user.getStatus() != UserStatus.APPROVED) {
            return switch (user.getStatus()) {
                case PENDING -> "Login failed. User status: PENDING (awaiting approval)";
                case REJECTED -> "Login failed. User status: REJECTED";
                case BLOCKED -> "Login failed. User status: BLOCKED";
                case INACTIVE -> "Login failed. User status: INACTIVE";
                default -> "Login failed. User status: %s".formatted(user.getStatus());
            };
        }

        return "Successful Login...";
    }



    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: %s".formatted(email)));
    }

    @Override
    @Transactional
    public String register(UserDetailDto userDetailDto) {
        validateUserDetails(userDetailDto);

        User user = mapToEntity(userDetailDto);
        handleDealerKyc(user, userDetailDto);

        user = userRepository.save(user);

        Otp otp = createOtp(user);
        otpRepository.save(otp);

        sendOtpNotifications(user, otp.getOtpCode());

        return "OTP sent to email and mobile. Please verify.";
    }

    // =========================
    // Private helper methods
    // =========================

    private void validateUserDetails(UserDetailDto dto) {
        if (dto == null) throw new InvalidCredentialsException("Invalid User Details");

        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail()))
            throw new UserAlreadyExistsException("Email already exists!");
        if (dto.getPhone() != null && userRepository.existsByPhone(dto.getPhone()))
            throw new UserAlreadyExistsException("Phone already exists!");
        if (dto.getUsername() != null && userRepository.existsByEmail(dto.getUsername()))
            throw new UserAlreadyExistsException("Username already exists!");

        if (dto.getPassword() != null && dto.getRetypePassword() != null &&
                !dto.getPassword().equals(dto.getRetypePassword())) {
            throw new InvalidCredentialsException("Password and Retype Password do not match!");
        }

        if (dto.getDob() != null && dto.getDob().isAfter(LocalDate.now()))
            throw new InvalidCredentialsException("Date of Birth cannot be in the future!");

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            String role = dto.getRole().trim().toUpperCase();
            if (!List.of("ADMIN", "SELLER", "DEALER", "BUYER").contains(role)) {
                throw new InvalidRoleException("Invalid role: %s".formatted(role));
            }
        }
    }

    private User mapToEntity(UserDetailDto dto) {
        User user = modelMapper.map(dto, User.class);

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            user.setRole(Set.of(dto.getRole().trim().toUpperCase()));
        }

        user.setActive(false);
        user.setDob(dto.getDob());

        return user;
    }

    private void handleDealerKyc(User user, UserDetailDto dto) {
        if (user.getRole() != null && user.getRole().contains("DEALER")) {
            MultipartFile file = dto.getDocument();
            if (file == null || file.isEmpty())
                throw new KycDocumentException("KYC document is required for DEALER!");

            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.matches(".*\\.(pdf|jpg|jpeg|png)$")) {
                throw new KycDocumentException("Only PDF/JPG/JPEG/PNG allowed for KYC");
            }

            try {
                User tempUser = userRepository.saveAndFlush(user);
                Path userDir = Path.of("/opt/app/dealer/kyc", String.valueOf(tempUser.getId()));
                Files.createDirectories(userDir);
                Path filePath = userDir.resolve(fileName);
                Files.write(filePath, file.getBytes());
                tempUser.setDocument("dealer/kyc/%d/%s".formatted(tempUser.getId(), fileName));
                user.setDocument(tempUser.getDocument());
            } catch (IOException e) {
                throw new KycDocumentException("Failed to save KYC document%s".formatted(e));
            }
        }
    }

    private Otp createOtp(User user) {
        try {
            String otpCode = String.format("%06d", random.nextInt(1_000_000));
            Otp otp = new Otp();
            otp.setOtpCode(otpCode);
            otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
            otp.setUsed(false);
            otp.setUser(user);
            otp.setEmail(user.getEmail());
            otp.setPhone(user.getPhone());
            return otp;
        } catch (Exception e) {
            throw new OtpGenerationException("Failed to generate OTP%s".formatted(e));
        }
    }

    private void sendOtpNotifications(User user, String otpCode) {
        emailService.sendOtp(user.getEmail(), otpCode);
        smsService.sendOtp(user.getPhone(), otpCode);
    }

}
