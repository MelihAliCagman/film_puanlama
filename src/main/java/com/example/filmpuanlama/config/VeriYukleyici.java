package com.example.filmpuanlama.config;

import com.example.filmpuanlama.model.Degerlendirme;
import com.example.filmpuanlama.model.Film;
import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.DegerlendirmeRepository;
import com.example.filmpuanlama.repository.FilmRepository;
import com.example.filmpuanlama.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VeriYukleyici implements CommandLineRunner {

    @Autowired private FilmRepository filmRepo;
    @Autowired private KullaniciRepository kullaniciRepo; // Bunları birazdan oluşturacağız
    @Autowired private DegerlendirmeRepository degerlendirmeRepo; // Bunları birazdan oluşturacağız

    @Override
    public void run(String... args) throws Exception {
        // Filmler boşsa ekle
        if (filmRepo.count() == 0) {
            Film f1 = new Film();
            f1.setAd("Inception");
            f1.setYonetmen("Nolan");
            f1.setPosterUrl("https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg");
            f1.setKonu("Rüyalar...");
            filmRepo.save(f1);

            // 1. Kullanıcıyı oluştur (Melih)
            Kullanici k1 = new Kullanici();
            k1.setAdSoyad("Melih Ali");
            k1.setEmail("melih@test.com");
            k1.setSifre("1234");
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