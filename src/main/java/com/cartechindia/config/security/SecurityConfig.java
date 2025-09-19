package com.cartechindia.config.security;

import com.cartechindia.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                          SecurityRulesProperties rulesProperties, AccessDeniedHandler accessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
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


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Public endpoints
                    auth.requestMatchers(
                            "/user/login",
                            "/user/register",
                            "/dealer/login",
                            "/dealer/register",
                            "/car/add",
                            "/cartech/swagger-ui/**",
                            "/cartech/api-docs/**",
                            "/v3/api-docs/**",
                            "/swagger-ui.html/**",
                            "/swagger-ui/**",
                            "/otp/**"
                    ).permitAll();

                    //Dynamic rules from properties
                    for (SecurityRulesProperties.Rule rule : rulesProperties.getRules()) {
                        auth.requestMatchers(rule.getPattern())
                                .hasAnyRole(rule.getRoles().split(","));
                    }

                    // Default
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

}

