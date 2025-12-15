package com.example.filmpuanlama.repository;

import com.example.filmpuanlama.model.Takip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TakipRepository extends JpaRepository<Takip, Long> {

    // 1. Birinin takip ettikleri (Ben kimi takip ediyorum?)
    List<Takip> findByTakipEdenId(Long takipEdenId);

    // 2. Birinin takipçileri (Beni kim takip ediyor?) -- YENİ EKLENDİ
    List<Takip> findByTakipEdilenId(Long takipEdilenId);

    // 3. Zaten takip ediyor mu?
    boolean existsByTakipEdenIdAndTakipEdilenId(Long edenId, Long edilenId);

    // 4. Takipten çıkmak için kaydı bul
    Takip findByTakipEdenIdAndTakipEdilenId(Long edenId, Long edilenId);
}