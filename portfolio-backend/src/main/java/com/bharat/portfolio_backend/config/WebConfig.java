package com.bharat.portfolio_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableAsync
// Serialising PageImpl directly is unsupported and its JSON shape is not stable.
// VIA_DTO emits the documented PagedModel shape for every paginated endpoint.
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    private static final String API_PATH = "/v1/**";

    private final AdminKeyInterceptor adminKeyInterceptor;
    private final String[] allowedOrigins;

    public WebConfig(AdminKeyInterceptor adminKeyInterceptor,
                     @Value("${app.cors.allowed-origins:}") String allowedOrigins) {
        this.adminKeyInterceptor = adminKeyInterceptor;
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminKeyInterceptor).addPathPatterns(API_PATH);
    }

    /**
     * The portfolio frontend is served from a different origin, so browser calls
     * (including the X-Admin-Key header) need an explicit CORS allowance.
     * Configure the origins with app.cors.allowed-origins / CORS_ALLOWED_ORIGINS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (allowedOrigins.length == 0) {
            return;
        }
        registry.addMapping(API_PATH)
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
