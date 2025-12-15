package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.model.Film;
import com.example.filmpuanlama.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filmler")
@CrossOrigin
public class FilmController {

    @Autowired
    private FilmRepository filmRepository; // Değişken adını standartlaştırdık

    // 1. Tüm Filmleri Getir
    @GetMapping
    public List<Film> tumFilmleriGetir() {
        return filmRepository.findAll();
    }

    // 2. Yeni Film Ekle
    @PostMapping
    public Film filmEkle(@RequestBody Film yeniFilm) {
        return filmRepository.save(yeniFilm);
    }

    // 3. Film Silme (ARTIK STORED PROCEDURE KULLANIYOR)
    @DeleteMapping("/{id}")
    public void filmSil(@PathVariable Long id) {
        // Eski yöntem (Standart JPA):
        // filmRepository.deleteById(id);

        // YENİ YÖNTEM (Stored Procedure):
        // Veritabanındaki 'film_sil' prosedürünü tetikler.
        // Bu prosedür önce yorumları, sonra izleme listesini, en son filmi siler.
        filmRepository.filmiProsedurleSil(id);
    }

    // 4. Film Güncelleme (Admin Paneli İçin)
    @PutMapping("/{id}")
    public Film filmGuncelle(@PathVariable Long id, @RequestBody Film guncelFilm) {
        Film mevcut = filmRepository.findById(id).orElseThrow();

        // Alanları güncelle
        mevcut.setAd(guncelFilm.getAd());
        mevcut.setYonetmen(guncelFilm.getYonetmen());
        mevcut.setKonu(guncelFilm.getKonu());
        mevcut.setPosterUrl(guncelFilm.getPosterUrl());
        mevcut.setTur(guncelFilm.getTur());

        // HATA ÇIKARAN KISIM DÜZELTİLDİ:
        // Artık 'yayinTarihi' değil 'yapimYili' kullanıyoruz
        mevcut.setYapimYili(guncelFilm.getYapimYili());

        return filmRepository.save(mevcut);
    }
}