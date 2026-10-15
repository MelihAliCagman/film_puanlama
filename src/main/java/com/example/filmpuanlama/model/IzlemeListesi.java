package com.example.filmpuanlama.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "izleme_listesi",
       uniqueConstraints = @UniqueConstraint(columnNames = {"kullanici_id", "film_id"}))
public class IzlemeListesi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id")
    private Kullanici kullanici;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    @Column(name = "eklenme_tarihi")
    private LocalDateTime eklenmeTarihi = LocalDateTime.now();

    // GETTER VE SETTER'LAR
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Kullanici getKullanici() { return kullanici; }
    public void setKullanici(Kullanici kullanici) { this.kullanici = kullanici; }

    public Film getFilm() { return film; }
    public void setFilm(Film film) { this.film = film; }

    public LocalDateTime getEklenmeTarihi() { return eklenmeTarihi; }
    public void setEklenmeTarihi(LocalDateTime eklenmeTarihi) { this.eklenmeTarihi = eklenmeTarihi; }
}