package com.example.filmpuanlama.controller;

import com.example.filmpuanlama.model.Kullanici;
import com.example.filmpuanlama.model.Takip;
import com.example.filmpuanlama.repository.KullaniciRepository;
import com.example.filmpuanlama.repository.TakipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/takip")
@CrossOrigin
public class TakipController {

    @Autowired private TakipRepository takipRepo;
    @Autowired private KullaniciRepository kullaniciRepo;

    // 1. TAKİP ET
    @PostMapping("/et")
    public String takipEt(@RequestBody TakipIstegi istek) {
        if(takipRepo.existsByTakipEdenIdAndTakipEdilenId(istek.edenId, istek.edilenId)) {
            return "Zaten takip ediyorsun";
        }

        Kullanici eden = kullaniciRepo.findById(istek.edenId).orElseThrow();
        Kullanici edilen = kullaniciRepo.findById(istek.edilenId).orElseThrow();

        Takip t = new Takip();
        t.setTakipEden(eden);
        t.setTakipEdilen(edilen);
        takipRepo.save(t);
        return "Takip Edildi";
    }

    // 2. TAKİBİ BIRAK
    @PostMapping("/birak")
    public String takibiBirak(@RequestBody TakipIstegi istek) {
        Takip t = takipRepo.findByTakipEdenIdAndTakipEdilenId(istek.edenId, istek.edilenId);
        if(t != null) {
            takipRepo.delete(t);
            return "Takip Bırakıldı";
        }
        return "Hata: Takip bulunamadı";
    }

    // 3. TAKİP ETTİKLERİMİ GETİR (Following) -- YENİ
    @GetMapping("/ettikleri/{userId}")
    public List<Takip> getTakipEttikleri(@PathVariable Long userId) {
        return takipRepo.findByTakipEdenId(userId);
    }

    // 4. TAKİPÇİLERİMİ GETİR (Followers) -- YENİ
    @GetMapping("/takipcileri/{userId}")
    public List<Takip> getTakipcileri(@PathVariable Long userId) {
        return takipRepo.findByTakipEdilenId(userId);
    }
}

class TakipIstegi {
    public Long edenId;
    public Long edilenId;
}