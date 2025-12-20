package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kullanicilar")
@CrossOrigin
public class KullaniciController {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    // 1. GİRİŞ YAP (LOGIN)
    @PostMapping("/giris")
    public Kullanici girisYap(@RequestBody Kullanici istek) {
        // Bu email ile kayıtlı biri var mı?
        Kullanici bulunan = kullaniciRepository.findByEmail(istek.getEmail());

        // Kullanıcı varsa VE şifresi doğruysa?
        if (bulunan != null && bulunan.getSifre().equals(istek.getSifre())) {
            return bulunan; // Başarılı! Kullanıcıyı döndür
        } else {
            return null; // Başarısız
        }
    }

    // 2. KAYIT OL (REGISTER)
    @PostMapping("/kayit")
    public Kullanici kayitOl(@RequestBody Kullanici yeniKullanici) {
        // Basitçe kaydet (Normalde şifre kontrolü vs. yapılır)
        return kullaniciRepository.save(yeniKullanici);
    }
}