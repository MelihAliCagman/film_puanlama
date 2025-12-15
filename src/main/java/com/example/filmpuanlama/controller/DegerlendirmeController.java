package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.model.Degerlendirme;
import com.example.filmpuanlama.model.Film;
import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.DegerlendirmeRepository;
import com.example.filmpuanlama.repository.FilmRepository;
import com.example.filmpuanlama.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/degerlendirmeler")
@CrossOrigin
public class DegerlendirmeController {

    @Autowired
    private DegerlendirmeRepository degerlendirmeRepo;

    @Autowired
    private FilmRepository filmRepo;

    @Autowired
    private KullaniciRepository kullaniciRepo;

    // 1. Bir filmin tüm yorumlarını getir
    @GetMapping("/film/{filmId}")
    public List<Degerlendirme> getFilmYorumlari(@PathVariable Long filmId) {
        return degerlendirmeRepo.findByFilmId(filmId);
    }

    // 2. Yeni Yorum Yap
    @PostMapping
    public Degerlendirme yorumEkle(@RequestBody YorumIstegi istek) {
        // Film ve Kullanıcıyı bul
        Film film = filmRepo.findById(istek.filmId).orElseThrow();
        Kullanici kullanici = kullaniciRepo.findById(istek.kullaniciId).orElseThrow();

        // Yeni yorumu oluştur
        Degerlendirme yeniYorum = new Degerlendirme();
        yeniYorum.setFilm(film);
        yeniYorum.setKullanici(kullanici);
        yeniYorum.setYorum(istek.yorum);
        yeniYorum.setPuan(istek.puan);

        // Veritabanına kaydet (Trigger ortalamayı güncelleyecek)
        return degerlendirmeRepo.save(yeniYorum);
    }

    // 3. Yorum Silme (SENİN EKLEDİĞİN KISIM - ARTIK DOĞRU YERDE)
    @DeleteMapping("/{id}")
    public void yorumSil(@PathVariable Long id) {
        degerlendirmeRepo.deleteById(id);
        // Not: Trigger sayesinde ortalama puan otomatik güncellenecek!
    }

} // <--- Controller sınıfı burada bitiyor

// --- Yardımcı Sınıf ---
// (Bu sınıfı en alta, ana sınıfın dışına koyuyoruz)
class YorumIstegi {
    public Long filmId;
    public Long kullaniciId;
    public String yorum;
    public int puan;
}