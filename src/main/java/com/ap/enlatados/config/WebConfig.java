package com.ap.enlatados.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")            // aplica a todas las rutas
            .allowedOrigins("*")          // permite cualquier origen
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") 
            .allowedHeaders("*")          // permite todos los encabezados
            .allowCredentials(false)      // si no usas cookies/auth basica
            .maxAge(3600);                // cachea la respuesta preflight 1h
    }
}