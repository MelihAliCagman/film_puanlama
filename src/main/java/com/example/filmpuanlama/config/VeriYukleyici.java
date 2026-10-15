package com.example.filmpuanlama.config;

import com.example.filmpuanlama.model.Degerlendirme;
import com.example.filmpuanlama.model.Film;
import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.DegerlendirmeRepository;
import com.example.filmpuanlama.repository.FilmRepository;
import com.example.filmpuanlama.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class VeriYukleyici implements CommandLineRunner {

    @Autowired private FilmRepository filmRepo;
    @Autowired private KullaniciRepository kullaniciRepo;
    @Autowired private DegerlendirmeRepository degerlendirmeRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Filmler boşsa ekle
        if (filmRepo.count() == 0) {
            Film f1 = new Film();
            f1.setAd("Inception");
            f1.setYonetmen("Christopher Nolan");
            f1.setPosterUrl("https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg");
            f1.setKonu("Rüyalar içinde rüya: bir hırsız, hedefin bilinçaltına fikir yerleştirmekle görevlendirilir.");
            f1.setOyuncular("Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page");
            f1.setYapimYili(2010);
            f1.setTur("Bilim Kurgu");
            filmRepo.save(f1);

            // 1. Kullanıcıyı oluştur (Melih)
            Kullanici k1 = new Kullanici();
            k1.setAdSoyad("Melih Ali");
            k1.setEmail("melih@test.com");
            k1.setSifre(passwordEncoder.encode("1234"));
            k1.setRol("USER");
            kullaniciRepo.save(k1);

            // 2. Yorum Yap
            Degerlendirme d1 = new Degerlendirme();
            d1.setFilm(f1);      // Hangi Film? -> Inception
            d1.setKullanici(k1); // Kim? -> Melih
            d1.setPuan(9);
            d1.setYorum("Hayatımda izlediğim en iyi filmdi!");
            degerlendirmeRepo.save(d1);

            System.out.println("--- Örnek Kullanıcı ve Yorum Eklendi ---");
        }
    }
}