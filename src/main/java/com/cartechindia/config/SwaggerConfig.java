package com.cartechindia.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Car Tech India API",
                version = "1.0.0",
                description = "API documentation for Car Tech India platform",
                contact = @Contact(name = "Car Tech India Support", email = "support@cartechindia.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")
        ),
        servers = { @Server(url = "/cartech", description = "Local Development Server") },
        security = { @SecurityRequirement(name = "bearerAuth") }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
    // No additional beans needed if @OpenAPIDefinition is sufficient
}
