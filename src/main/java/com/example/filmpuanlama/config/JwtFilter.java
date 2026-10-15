package com.example.filmpuanlama.config;

import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.KullaniciRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/** "Authorization: Bearer token" basligini okur; rol her istekte veritabanindan alinir. */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final KullaniciRepository kullaniciRepo;

    public JwtFilter(JwtService jwtService, KullaniciRepository kullaniciRepo) {
        this.jwtService = jwtService;
        this.kullaniciRepo = kullaniciRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String baslik = request.getHeader("Authorization");
        if (baslik != null && baslik.startsWith("Bearer ")) {
            Long id = jwtService.dogrula(baslik.substring(7));
            if (id != null) {
                Kullanici k = kullaniciRepo.findById(id).orElse(null);
                if (k != null) {
                    String rol = "ADMIN".equals(k.getRol()) ? "ROLE_ADMIN" : "ROLE_USER";
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(k.getId(), null, List.of(new SimpleGrantedAuthority(rol))));
                }
            }
        }
        chain.doFilter(request, response);
    }
}
