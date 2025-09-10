package com.cartechindia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:/opt/cartech/uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ensure path ends with slash and convert to file: URI
        String resourceLocation = "file:" + (uploadDir.endsWith("/") ? uploadDir : uploadDir + "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600); // optional
    }
}
