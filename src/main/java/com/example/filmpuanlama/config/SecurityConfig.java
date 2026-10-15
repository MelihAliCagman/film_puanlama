package com.example.filmpuanlama.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtreZinciri(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
            .csrf(c -> c.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/kullanicilar/giris", "/api/kullanicilar/kayit").permitAll()
                .requestMatchers("/error").permitAll()
                // Film ekleme / guncelleme / silme yalnizca yonetici
                .requestMatchers(HttpMethod.POST, "/api/filmler/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/filmler/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/filmler/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
