package com.example.filmpuanlama.repository;

import com.example.filmpuanlama.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Veri değiştiren işlem
import org.springframework.data.jpa.repository.Query;   // Özel sorgu
import org.springframework.data.repository.query.Param; // Parametre göndermek için
import org.springframework.transaction.annotation.Transactional; // İşlem güvenliği

public interface FilmRepository extends JpaRepository<Film, Long> {

    // STANDART JPA METOTLARI DIŞINDA:

    // Veritabanındaki "film_sil" prosedürünü çağıran özel metot
    @Modifying // Bu işlem veritabanında değişiklik yapar (SELECT değildir)
    @Transactional // Hata olursa işlemi geri al (Rollback)
    @Query(value = "CALL film_sil(:filmId)", nativeQuery = true)
    void filmiProsedurleSil(@Param("filmId") Long filmId);
}