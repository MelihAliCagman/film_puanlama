package com.example.filmpuanlama.repository;

import com.example.filmpuanlama.model.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {
    // Veritabanında email'e göre arama yapmamızı sağlayan sihirli metot:
    Kullanici findByEmail(String email);
}