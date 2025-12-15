package com.example.filmpuanlama.repository;

import com.example.filmpuanlama.model.Degerlendirme;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DegerlendirmeRepository extends JpaRepository<Degerlendirme, Long> {
    // Bir filme ait yorumları bulmak için özel metot
    List<Degerlendirme> findByFilmId(Long filmId);
}