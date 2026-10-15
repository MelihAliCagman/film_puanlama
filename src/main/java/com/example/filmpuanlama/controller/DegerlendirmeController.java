package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.config.Oturum;
import com.example.filmpuanlama.model.Degerlendirme;
import com.example.filmpuanlama.model.Film;
import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.repository.DegerlendirmeRepository;
import com.example.filmpuanlama.repository.FilmRepository;
import com.example.filmpuanlama.repository.KullaniciRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/degerlendirmeler")
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
        if (istek.puan < 1 || istek.puan > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Puan 1 ile 10 arasında olmalıdır");
        }
        Film film = filmRepo.findById(istek.filmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film bulunamadı"));
        Kullanici kullanici = kullaniciRepo.findById(Oturum.kullaniciId()) // kimlik istekten degil token'dan alinir
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));

        // Yeni yorumu oluştur
        Degerlendirme yeniYorum = new Degerlendirme();
        yeniYorum.setFilm(film);
        yeniYorum.setKullanici(kullanici);
        yeniYorum.setYorum(istek.yorum);
        yeniYorum.setPuan(istek.puan);

        // Veritabanına kaydet (Trigger ortalamayı güncelleyecek)
        return degerlendirmeRepo.save(yeniYorum);
    }

    // 3. Yorum Silme
    @DeleteMapping("/{id}")
    public void yorumSil(@PathVariable Long id) {
        Degerlendirme d = degerlendirmeRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Yorum bulunamadı"));
        if (!Oturum.adminMi() && !d.getKullanici().getId().equals(Oturum.kullaniciId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu yorumu silme yetkin yok");
        }
        degerlendirmeRepo.deleteById(id);
        // Not: Trigger sayesinde ortalama puan otomatik güncellenecek!
    }

}

// İstek gövdesi
class YorumIstegi {
    public Long filmId;
    public Long kullaniciId;
    public String yorum;
    public int puan;
}