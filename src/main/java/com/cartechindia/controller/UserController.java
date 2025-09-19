package com.cartechindia.controller;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.exception.InvalidCredentialsException;
import com.cartechindia.service.LoginService;
import com.cartechindia.service.OtpService;
import com.cartechindia.service.UserService;
import com.cartechindia.service.impl.CustomUserDetails;
import com.cartechindia.service.impl.CustomUserDetailsService;
import com.cartechindia.service.impl.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
@Tag(name = "User API", description = "Endpoints for user authentication and registration")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService uds;
    private final LoginService loginService;
    private final OtpService otpService;

    public UserController(UserService userService, AuthenticationManager authenticationManager,
                          JwtService jwtService, CustomUserDetailsService uds, LoginService loginService, OtpService otpService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.uds = uds;
        this.loginService = loginService;
        this.otpService = otpService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@org.springframework.web.bind.annotation.RequestBody LoginDetailDto req,
                                                     HttpServletRequest request) {
        boolean success = false;
        String token = null;
        Long userId = null;
        Collection<String> role;

        try {
            //Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            //Load user details
            UserDetails user = uds.loadUserByUsername(req.getEmail());
            token = jwtService.generateToken(user.getUsername(), user.getAuthorities());

            //Extract userId if CustomUserDetails exposes it
            if (user instanceof CustomUserDetails customUser) {
                userId = customUser.getId();
            }

            role = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            success = true;

//            return ResponseEntity.ok(Map.of("token", token));

            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "role", role.toString()
                    )
            );

        } catch (Exception e) {
            throw new InvalidCredentialsException("Email/Password Invalid!");
        } finally {
            //Record login attempt with latitude/longitude from request DTO
            loginService.recordLogin(
                    userId != null ? userId : -1L,           // -1 if login failed
                    request.getRemoteAddr(),                 // IP Address
                    request.getHeader("User-Agent"),         // Device info
                    success,
                    req.getLongitude(),                      //longitude from request
                    req.getLatitude()                        //latitude from request
            );
        }
    }

    @Operation(
            summary = "Register a user",
            description = "Registers a new user. Supports optional file uploads for DEALER (e.g. KYC documents).",
            requestBody = @RequestBody(
                    description = "User details with optional PDF/image file",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UserDetailDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully",
                            content = @Content(schema = @Schema(example = "User registered successfully"))),
                    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
            }
    )
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(
            @Parameter(description = "User details in multipart form data with optional documents")
            @ModelAttribute UserDetailDto userDetailDto) {
        String status = userService.register(userDetailDto);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

}
