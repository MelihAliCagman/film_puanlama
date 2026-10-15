package com.example.filmpuanlama.repository;

import com.example.filmpuanlama.model.IzlemeListesi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IzlemeListesiRepository extends JpaRepository<IzlemeListesi, Long> {

    // 1. Bir kullanıcının tüm izleme listesini getir
    List<IzlemeListesi> findByKullaniciId(Long kullaniciId);

    // 2. Bir kullanıcı bu filmi zaten eklemiş mi? (Çift kaydı önlemek için)
    boolean existsByKullaniciIdAndFilmId(Long kullaniciId, Long filmId);

    // 4. Film silinirken ilgili kayıtları temizle
    void deleteByFilmId(Long filmId);

    // 3. Listeden çıkarmak için kaydı bul
    IzlemeListesi findByKullaniciIdAndFilmId(Long kullaniciId, Long filmId);
}