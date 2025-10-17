package com.cartechindia.config.security;

import com.cartechindia.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService uds;
    private final SecurityRulesProperties rulesProperties;
    private final AccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthFilter filter,
                          CustomUserDetailsService uds,
                          SecurityRulesProperties rulesProperties,
                          AccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtAuthFilter = filter;
        this.uds = uds;
        this.rulesProperties = rulesProperties;
        this.accessDeniedHandler = accessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ Modern Spring Security 6+ CORS setup (no deprecated .and())
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ modern CORS setup
                .csrf(AbstractHttpConfigurer::disable) // ✅ disable CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll(); // allow preflight

                    // Public endpoints
                    auth.requestMatchers(
                            "/otp/**",
                            "/api/cars/**",
                            "/cartech/api/otp/**",
                            "/api/users/login",
                            "/api/users/register",
                            "/dealer/login",
                            "/dealer/register",
                            "/car/add",
                            "/cartech/swagger-ui/**",
                            "/cartech/api-docs/**",
                            "/v3/api-docs/**",
                            "/swagger-ui.html/**",
                            "/swagger-ui/**"
                    ).permitAll();

                    // Dynamic rules from properties
                    for (SecurityRulesProperties.Rule rule : rulesProperties.getRules()) {
                        auth.requestMatchers(rule.getPattern())
                                .hasAnyRole(rule.getRoles().split(","));
                    }

                    // Default: require authentication
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .userDetailsService(uds)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ CORS configuration source (modern lambda-based)
     * Reads allowed origins here directly.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // ✅ Use allowedOriginPatterns instead of allowedOrigins
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://98.80.120.96", "http://98.80.120.96:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
