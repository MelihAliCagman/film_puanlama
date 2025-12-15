package com.example.filmpuanlama.model;

import jakarta.persistence.*;
import lombok.Data; // Eğer lombok çalışıyorsa (yoksa getter/setter elle yaz)

@Entity
@Table(name = "kullanicilar")
public class Kullanici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adSoyad;

    @Column(unique = true) // Aynı mail ile iki kişi kayıt olamaz
    private String email;

    private String sifre; // Normalde şifreli tutulur ama şimdilik düz yazalım

    private String rol; // "ADMIN" veya "USER"

    // --- Getter ve Setter'lar (Lombok yoksa) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}