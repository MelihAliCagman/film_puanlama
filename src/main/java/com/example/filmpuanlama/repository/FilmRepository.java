package com.example.filmpuanlama.repository;

import com.example.filmpuanlama.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {

    // 1. Tüm Zamanların En İyileri (Procedure Çağırısı)
    @Query(value = "SELECT * FROM en_cok_izlenenleri_getir(:limitSayisi)", nativeQuery = true)
    List<Film> enCokIzlenenler(@Param("limitSayisi") int limitSayisi);

    // 2. Bu Haftanın Trendleri
    @Query(value = "SELECT * FROM bu_hafta_en_cok_izlenenleri_getir(:limitSayisi)", nativeQuery = true)
    List<Film> haftaninTrendleri(@Param("limitSayisi") int limitSayisi);

    // 3. Arkadaşların İzledikleri
    @Query(value = "SELECT * FROM arkadaslarin_bu_hafta_izledikleri(:kullaniciId, :limitSayisi)", nativeQuery = true)
    List<Film> arkadaslarinOnerileri(@Param("kullaniciId") Long kullaniciId, @Param("limitSayisi") int limitSayisi);
}