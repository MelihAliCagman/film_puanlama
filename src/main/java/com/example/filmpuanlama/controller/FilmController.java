package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.model.Film;
import com.example.filmpuanlama.repository.FilmRepository;
import com.example.filmpuanlama.config.Oturum;
import com.example.filmpuanlama.repository.DegerlendirmeRepository;
import com.example.filmpuanlama.repository.IzlemeListesiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/filmler")
public class FilmController {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private DegerlendirmeRepository degerlendirmeRepo;

    @Autowired
    private IzlemeListesiRepository izlemeRepo;

    // Tüm filmleri getir
    @GetMapping
    public List<Film> tumFilmleriGetir() {
        return filmRepository.findAll();
    }

    // Yeni film ekle
    @PostMapping
    public Film filmEkle(@RequestBody Film film) {
        if (film.getAd() == null || film.getAd().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film adı zorunludur");
        }
        film.setId(null);
        film.setPuan(0); // Ortalama puanı trigger hesaplar
        return filmRepository.save(film);
    }

    // Film sil
    @DeleteMapping("/{id}")
    @Transactional
    public void filmSil(@PathVariable Long id) {
        // Yorum ve izleme listesi kayıtları da silinir (aksi halde FK hatası oluşur)
        degerlendirmeRepo.deleteByFilmId(id);
        izlemeRepo.deleteByFilmId(id);
        filmRepository.deleteById(id);
    }

    // --- YENİ: FİLM GÜNCELLEME ÖZELLİĞİ ---
    @PutMapping("/{id}")
    public Film filmGuncelle(@PathVariable Long id, @RequestBody Film guncelFilm) {
        Film mevcut = filmRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film bulunamadı"));
        mevcut.setAd(guncelFilm.getAd());
        mevcut.setYonetmen(guncelFilm.getYonetmen());
        mevcut.setOyuncular(guncelFilm.getOyuncular());
        mevcut.setKonu(guncelFilm.getKonu());
        mevcut.setYapimYili(guncelFilm.getYapimYili());
        mevcut.setTur(guncelFilm.getTur());
        mevcut.setPosterUrl(guncelFilm.getPosterUrl());
        return filmRepository.save(mevcut);
    }

    // --- KEŞFET EKRANI İÇİN ÖZEL SORGU Endpoint'leri ---

    @GetMapping("/populer")
    public List<Film> getPopulerFilmler() {
        return filmRepository.enCokIzlenenler(10);
    }

    @GetMapping("/trendler")
    public List<Film> getHaftaninTrendleri() {
        return filmRepository.haftaninTrendleri(10);
    }

    @GetMapping("/arkadas-onerileri/{kullaniciId}")
    public List<Film> getArkadasOnerileri(@PathVariable Long kullaniciId) {
        if (!kullaniciId.equals(Oturum.kullaniciId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yalnızca kendi önerilerini görebilirsin");
        }
        return filmRepository.arkadaslarinOnerileri(kullaniciId, 10);
    }
}