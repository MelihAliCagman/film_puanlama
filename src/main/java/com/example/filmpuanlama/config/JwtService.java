package com.example.filmpuanlama.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey anahtar;
    private final long gecerlilikMs;

    public JwtService(@Value("${app.jwt.secret:}") String secret,
                      @Value("${app.jwt.expiration-hours:24}") long saat) {
        if (secret == null || secret.length() < 32) {
            byte[] rastgele = new byte[48];
            new SecureRandom().nextBytes(rastgele);
            this.anahtar = Keys.hmacShaKeyFor(rastgele);
            log.warn("JWT_SECRET tanimli degil (en az 32 karakter): rastgele anahtar uretildi, "
                    + "uygulama yeniden baslayinca oturumlar gecersiz olur.");
        } else {
            this.anahtar = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        this.gecerlilikMs = saat * 3600_000L;
    }

    public String uret(Long kullaniciId) {
        Date simdi = new Date();
        return Jwts.builder()
                .subject(String.valueOf(kullaniciId))
                .issuedAt(simdi)
                .expiration(new Date(simdi.getTime() + gecerlilikMs))
                .signWith(anahtar)
                .compact();
    }

    /** Gecerliyse kullanici id'sini, degilse null dondurur. */
    public Long dogrula(String token) {
        try {
            Claims c = Jwts.parser().verifyWith(anahtar).build().parseSignedClaims(token).getPayload();
            return Long.valueOf(c.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
