package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.config.JwtService;
import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kullanicilar")
public class KullaniciController {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // 1. GİRİŞ YAP (LOGIN)
    @PostMapping("/giris")
    public Map<String, Object> girisYap(@RequestBody Kullanici istek) {
        Kullanici bulunan = istek.getEmail() == null ? null : kullaniciRepository.findByEmail(istek.getEmail().trim());

        if (bulunan == null || istek.getSifre() == null || !sifreDogruMu(bulunan, istek.getSifre())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email veya şifre hatalı");
        }
        Map<String, Object> cevap = new LinkedHashMap<>();
        cevap.put("id", bulunan.getId());
        cevap.put("adSoyad", bulunan.getAdSoyad());
        cevap.put("email", bulunan.getEmail());
        cevap.put("rol", bulunan.getRol());
        cevap.put("token", jwtService.uret(bulunan.getId()));
        return cevap;
    }

    // 2. KAYIT OL (REGISTER)
    @PostMapping("/kayit")
    public Kullanici kayitOl(@RequestBody Kullanici yeni) {
        if (bos(yeni.getAdSoyad()) || bos(yeni.getEmail()) || bos(yeni.getSifre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ad soyad, email ve şifre zorunludur");
        }
        String email = yeni.getEmail().trim();
        if (kullaniciRepository.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu email ile zaten bir hesap var");
        }

        Kullanici k = new Kullanici();
        k.setAdSoyad(yeni.getAdSoyad().trim());
        k.setEmail(email);
        k.setSifre(passwordEncoder.encode(yeni.getSifre()));
        k.setRol("USER"); // Rol istemciden alınmaz; yönetici yapmak için veritabanından güncellenir
        return kullaniciRepository.save(k);
    }

    // BCrypt kontrolü. Eski (düz metin) kayıtlar ilk başarılı girişte otomatik hashlenir.
    private boolean sifreDogruMu(Kullanici k, String girilen) {
        String kayitli = k.getSifre();
        if (kayitli == null) return false;
        if (kayitli.startsWith("$2")) {
            return passwordEncoder.matches(girilen, kayitli);
        }
        if (kayitli.equals(girilen)) {
            k.setSifre(passwordEncoder.encode(girilen));
            kullaniciRepository.save(k);
            return true;
        }
        return false;
    }

    private boolean bos(String s) {
        return s == null || s.isBlank();
    }
}
