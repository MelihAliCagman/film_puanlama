package com.example.filmpuanlama.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "izleme_listesi")
public class IzlemeListesi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate eklenmeTarihi = LocalDate.now();

    // --- İLİŞKİLER ---

    @ManyToOne
    @JoinColumn(name = "kullanici_id")
    private Kullanici kullanici;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    // --- Getter ve Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getEklenmeTarihi() { return eklenmeTarihi; }
    public void setEklenmeTarihi(LocalDate eklenmeTarihi) { this.eklenmeTarihi = eklenmeTarihi; }
    public Kullanici getKullanici() { return kullanici; }
    public void setKullanici(Kullanici kullanici) { this.kullanici = kullanici; }
    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }
}