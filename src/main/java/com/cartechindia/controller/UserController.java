package com.cartechindia.controller;

import com.cartechindia.dto.LoginDetailDto;
import com.cartechindia.dto.UserDetailDto;
import com.cartechindia.exception.InvalidCredentialsException;
import com.cartechindia.service.LoginService;
import com.cartechindia.service.UserService;
import com.cartechindia.serviceImpl.CustomUserDetails;
import com.cartechindia.serviceImpl.CustomUserDetailsService;
import com.cartechindia.serviceImpl.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    final private UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService uds;
    private final LoginService loginService;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsService uds, LoginService loginService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.uds = uds;
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDetailDto req,
                                                     HttpServletRequest request) {
        boolean success = false;
        String token = null;
        Long userId = null;

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

            success = true;

            return ResponseEntity.ok(Map.of("token", token));

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


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody UserDetailDto userDetailDto) {
        String status = userService.register(userDetailDto);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @GetMapping("/home")
    public ResponseEntity<String> userHome(){
        return new ResponseEntity<>("Welcome to User Page", HttpStatus.OK);
    }
}
