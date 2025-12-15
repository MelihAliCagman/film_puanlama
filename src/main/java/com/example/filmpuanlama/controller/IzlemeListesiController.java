package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.model.Film;
import com.example.filmpuanlama.model.IzlemeListesi;
import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.FilmRepository;
import com.example.filmpuanlama.repository.IzlemeListesiRepository;
import com.example.filmpuanlama.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/izleme-listesi")
@CrossOrigin
public class IzlemeListesiController {

    @Autowired private IzlemeListesiRepository izlemeRepo;
    @Autowired private FilmRepository filmRepo;
    @Autowired private KullaniciRepository kullaniciRepo;

    // 1. Kullanıcının Listesini Getir
    @GetMapping("/{kullaniciId}")
    public List<IzlemeListesi> listeyiGetir(@PathVariable Long kullaniciId) {
        return izlemeRepo.findByKullaniciId(kullaniciId);
    }

    // 2. Listeye Ekle
    @PostMapping("/ekle")
    public String listeyeEkle(@RequestBody Istek istek) {
        // Zaten ekli mi?
        if (izlemeRepo.existsByKullaniciIdAndFilmId(istek.kullaniciId, istek.filmId)) {
            return "Film zaten listede!";
        }

        Film film = filmRepo.findById(istek.filmId).orElseThrow();
        Kullanici kullanici = kullaniciRepo.findById(istek.kullaniciId).orElseThrow();

        IzlemeListesi yeniKayit = new IzlemeListesi();
        yeniKayit.setFilm(film);
        yeniKayit.setKullanici(kullanici);

        izlemeRepo.save(yeniKayit);
        return "Listeye Eklendi";
    }

    // 3. Listeden Çıkar
    @PostMapping("/cikar")
    public String listedenCikar(@RequestBody Istek istek) {
        IzlemeListesi kayit = izlemeRepo.findByKullaniciIdAndFilmId(istek.kullaniciId, istek.filmId);
        if (kayit != null) {
            izlemeRepo.delete(kayit);
            return "Listeden Çıkarıldı";
        }
        return "Kayıt bulunamadı";
    }
}

// React'ten gelecek veri formatı
class Istek {
    public Long kullaniciId;
    public Long filmId;
}