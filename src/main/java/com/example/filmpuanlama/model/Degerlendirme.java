package com.example.filmpuanlama.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "degerlendirmeler")
public class Degerlendirme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int puan; // 1-10 arası

    @Column(length = 1000)
    private String yorum;

    private LocalDateTime yapildigiTarih = LocalDateTime.now();

    // --- İLİŞKİLER (RELATIONS) ---
    // Veritabanı dersinin en önemli kısmı burası: Foreign Key'ler

    @ManyToOne // Bir filmde çokça değerlendirme olabilir
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne // Bir kullanıcı çokça değerlendirme yapabilir
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    // --- Getter ve Setter'lar ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getPuan() { return puan; }
    public void setPuan(int puan) { this.puan = puan; }
    public String getYorum() { return yorum; }
    public void setYorum(String yorum) { this.yorum = yorum; }
    public LocalDateTime getYapildigiTarih() { return yapildigiTarih; }
    public void setYapildigiTarih(LocalDateTime yapildigiTarih) { this.yapildigiTarih = yapildigiTarih; }
    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }
    public Kullanici getKullanici() { return kullanici; }
    public void setKullanici(Kullanici kullanici) { this.kullanici = kullanici; }
}