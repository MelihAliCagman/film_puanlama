package com.example.filmpuanlama.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "filmler")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ad;

    private String yonetmen;

    @Column(length = 2000)
    private String konu;

    @Column(name = "oyuncular")
    private String oyuncular; // Örn: "Tim Robbins, Morgan Freeman"

    private int yapimYili;

    private double puan;

    private String tur;

    private String posterUrl;

    // --- Getter ve Setter Metotları ---
    // (Lombok yerine bunları elle yazdık, artık hata vermeyecek)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getYonetmen() {
        return yonetmen;
    }

    public void setYonetmen(String yonetmen) {
        this.yonetmen = yonetmen;
    }

    public String getKonu() {
        return konu;
    }

    public void setKonu(String konu) {
        this.konu = konu;
    }

    public int getYapimYili() {
        return yapimYili;
    }

    public void setYapimYili(int yapimYili) {
        this.yapimYili = yapimYili;
    }

    public double getPuan() {
        return puan;
    }

    public void setPuan(double puan) {
        this.puan = puan;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    // ... Getter ve Setter metotlarını ekle (veya Lombok varsa kendi halleder) ...
    public String getOyuncular() {
        return oyuncular;
    }

    public void setOyuncular(String oyuncular) {
        this.oyuncular = oyuncular;
    }
}