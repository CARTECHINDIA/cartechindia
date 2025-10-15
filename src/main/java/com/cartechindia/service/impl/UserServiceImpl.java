package com.cartechindia.service.impl;

import com.cartechindia.constraints.DocumentStatus;
import com.cartechindia.constraints.UserStatus;
import com.cartechindia.dto.request.LoginRequestDto;
import com.cartechindia.dto.request.UserRequestDto;
import com.cartechindia.dto.response.UserResponseDto;
import com.cartechindia.entity.Document;
import com.cartechindia.entity.Otp;
import com.cartechindia.entity.User;
import com.cartechindia.exception.*;
import com.cartechindia.repository.DocumentRepository;
import com.cartechindia.repository.OtpRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.EmailService;
import com.cartechindia.service.SmsService;
import com.cartechindia.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final Random random = new Random();

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ModelMapper modelMapper,
                           OtpRepository otpRepository,
                           EmailService emailService,
                           SmsService smsService,
                           DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.documentRepository = documentRepository;
    }

    // =========================
    // Dealer/User Management
    // =========================

    @Override
    @Transactional
    public void updateDealerStatus(Long userId, UserStatus status, String remarks) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        if (user.getRole() == null || !user.getRole().contains("DEALER")) {
            throw new RuntimeException("User is not a dealer");
        }

        if (status != UserStatus.ACTIVE && status != UserStatus.REJECTED) {
            throw new RuntimeException("Invalid status. Only APPROVED or REJECTED allowed.");
        }

        user.setStatus(status);
        if (status == UserStatus.ACTIVE) {
            user.setActive(true);
        }

        userRepository.save(user);
    }

    @Override
    public String getDocumentPathByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getDocument() == null || user.getDocument().isBlank()) {
            throw new RuntimeException("No KYC document found for user");
        }

        // ✅ Use actual saved path instead of hardcoding /opt/app
        return user.getDocument();
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        user.setStatus(status);
        user.setActive(status == UserStatus.ACTIVE);

        userRepository.save(user);
    }

    @Override
    public List<User> getUnapprovedUsers() {
        return userRepository.findByStatusNot(UserStatus.ACTIVE);
    }

    // =========================
    // Authentication
    // =========================

    @Override
    public String login(LoginRequestDto loginDetailDto) {
        User user = userRepository.findByEmail(loginDetailDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email/Password Invalid!"));

        if (!passwordEncoder.matches(loginDetailDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email/Password Invalid!");
        }

        if (!user.isActive()) {
            return "Login failed. Your account is inactive. Current status: " + user.getStatus();
        }

        switch (user.getStatus()) {
            case ACTIVE:
                return "Successful Login. Welcome!";
            case PENDING:
                return user.getRole() != null && user.getRole().contains("DEALER")
                        ? "Login failed. Your account is pending admin approval."
                        : "Login failed. Your account is pending approval.";
            case REJECTED:
                return "Login failed. Your account has been rejected.";
            case SUSPENDED:
                return "Login failed. Your account is blocked. Contact admin.";
            case INACTIVE:
                return "Login failed. Your account is inactive.";
            default:
                return "Login failed. Your account status does not allow login.";
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: %s".formatted(email)));
    }

    // =========================
    // Registration & KYC
    // =========================

    @Override
    @Transactional
    public String register(UserRequestDto userDetailDto) {
        validateUserDetails(userDetailDto);

        User user = mapToEntity(userDetailDto);
        user = userRepository.save(user);

        if (user.getRole().contains("DEALER")) {
            handleDealerKyc(user, userDetailDto);
            user.setStatus(UserStatus.PENDING);
            user.setActive(false);
        } else {
            user.setStatus(UserStatus.ACTIVE);
            user.setActive(true);
        }

        userRepository.save(user);

        Otp otp = createOtp(user);
        otpRepository.save(otp);

        sendOtpNotifications(user, otp.getOtpCode());

        return "OTP sent to email and mobile. Please verify.";
    }

    private void validateUserDetails(UserRequestDto dto) {
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

    private User mapToEntity(UserRequestDto dto) {
        User user = modelMapper.map(dto, User.class);
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            user.setRole(new HashSet<>(Collections.singleton(dto.getRole().trim().toUpperCase())));
        } else {
            user.setRole(new HashSet<>());
        }

        user.setActive(false);
        user.setDob(dto.getDob());
        user.setCreatedDateTime(LocalDateTime.now());
        user.setUpdatedDateTime(LocalDateTime.now());
        user.setStatus(UserStatus.PENDING);
        return user;
    }

    private void handleDealerKyc(User user, UserRequestDto dto) {
        MultipartFile file = dto.getDocument();
        if (file == null || file.isEmpty()) {
            throw new KycDocumentException("KYC document is required for DEALER!");
        }

        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        if (!fileName.matches(".*\\.(pdf|jpg|jpeg|png)$")) {
            throw new KycDocumentException("Only PDF, JPG, JPEG, or PNG files are allowed.");
        }

        Path baseUploadDir = Path.of(uploadDir);
        Path dealerFolder = baseUploadDir.resolve("dealer/kyc/" + user.getId());
        Path filePath = dealerFolder.resolve(fileName);

        try {
            Files.createDirectories(dealerFolder);
            Files.write(filePath, file.getBytes());
            System.out.println("✅ Saved KYC document to: " + filePath);
        } catch (IOException e) {
            // ⚠️ Fallback to /tmp/uploads if primary fails
            try {
                Path fallbackDir = Path.of("/tmp/uploads/dealer/kyc/" + user.getId());
                Files.createDirectories(fallbackDir);
                filePath = fallbackDir.resolve(fileName);
                Files.write(filePath, file.getBytes());
                System.err.println("[WARN] Primary uploadDir failed (" + uploadDir + "). Fallback to " + filePath);
            } catch (IOException ex) {
                throw new KycDocumentException("Failed to save KYC document: " + ex.getMessage());
            }
        }

        user.setDocument(filePath.toString());

        Document document = new Document();
        document.setType("KYC");
        document.setFilePath(filePath.toString());
        document.setStatus(DocumentStatus.PENDING);
        document.setUser(user);
        document.setUploadedAt(LocalDateTime.now());

        documentRepository.save(document);
    }

    // =========================
    // OTP
    // =========================

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
            throw new OtpGenerationException("Failed to generate OTP: " + e.getMessage());
        }
    }

    private void sendOtpNotifications(User user, String otpCode) {
        emailService.sendOtp(user.getEmail(), otpCode);
        smsService.sendOtp(user.getPhone(), otpCode);
    }

    // =========================
    // Document Approval
    // =========================

    @Override
    @Transactional
    public Resource getUserDocumentForApproval(Long userId, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getDocument() == null || user.getDocument().isBlank()) {
            throw new RuntimeException("No KYC document uploaded for this user.");
        }

        if (action != null) {
            switch (action.toUpperCase()) {
                case "APPROVE":
                    updateUserStatus(userId, UserStatus.ACTIVE);
                    break;
                case "REJECT":
                    updateUserStatus(userId, UserStatus.REJECTED);
                    break;
                default:
                    throw new RuntimeException("Invalid action. Use APPROVE or REJECT.");
            }
        }

        try {
            String documentPath = user.getDocument(); // ✅ Use actual saved path
            Path path = Path.of(documentPath);
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Document not found or not accessible: " + documentPath);
            }

            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load document: " + e.getMessage());
        }
    }

    private UserResponseDto userToUseResponseDto(User user) {
        if (user == null) return null;

        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setCity(user.getCity());
        dto.setArea(user.getArea());
        dto.setAddress(user.getAddress());
        dto.setUsername(user.getUsername());
        dto.setDob(user.getDob());
        dto.setActive(user.isActive());
        dto.setStatus(user.getStatus().name());
        dto.setRole(user.getRole());
        dto.setDocument(user.getDocument());
        dto.setCreatedDateTime(user.getCreatedDateTime());
        dto.setUpdatedDateTime(user.getUpdatedDateTime());
        return dto;
    }
}
