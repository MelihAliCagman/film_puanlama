-- FilmBox veritabani nesneleri (saklı fonksiyonlar, prosedürler, trigger).
-- Uygulama her acilista calistirir; hepsi idempotenttir (CREATE OR REPLACE / DROP IF EXISTS).
-- Tablolar Hibernate (ddl-auto=update) tarafindan olusturulduktan sonra yurutulur.
-- Ifadeler, $$ bloklari yuzunden ^^^ ile ayrilir (bkz. spring.sql.init.separator).

-- 1) Ortalama puan: yorum eklenince/degisince/silinince filmler.puan guncellenir
CREATE OR REPLACE FUNCTION puan_hesapla()
RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
    hedef_film_id BIGINT;
    yeni_ortalama DOUBLE PRECISION;
BEGIN
    IF (TG_OP = 'DELETE') THEN
        hedef_film_id := OLD.film_id;
    ELSE
        hedef_film_id := NEW.film_id;
    END IF;

    SELECT ROUND(AVG(puan)::numeric, 1)
    INTO yeni_ortalama
    FROM degerlendirmeler
    WHERE film_id = hedef_film_id;

    UPDATE filmler
    SET puan = COALESCE(yeni_ortalama, 0)
    WHERE id = hedef_film_id;

    RETURN NULL;
END;
$function$
^^^

DROP TRIGGER IF EXISTS tetikleyici_puan_guncelle ON degerlendirmeler
^^^

CREATE TRIGGER tetikleyici_puan_guncelle
AFTER INSERT OR DELETE OR UPDATE ON degerlendirmeler
FOR EACH ROW EXECUTE FUNCTION puan_hesapla()
^^^

-- 2) Tum zamanlarin en cok izlenenleri (izleme listelerinde en cok yer alanlar)
CREATE OR REPLACE FUNCTION en_cok_izlenenleri_getir(limit_sayisi integer)
RETURNS SETOF filmler
LANGUAGE plpgsql
AS $function$
BEGIN
    RETURN QUERY
    SELECT f.*
    FROM filmler f
    JOIN izleme_listesi il ON f.id = il.film_id
    GROUP BY f.id
    ORDER BY COUNT(il.id) DESC
    LIMIT limit_sayisi;
END;
$function$
^^^

-- 3) Haftanin trendleri (son 7 gunde izleme listesine en cok eklenenler)
CREATE OR REPLACE FUNCTION bu_hafta_en_cok_izlenenleri_getir(limit_sayisi integer)
RETURNS SETOF filmler
LANGUAGE plpgsql
AS $function$
BEGIN
    RETURN QUERY
    SELECT f.*
    FROM filmler f
    JOIN izleme_listesi il ON f.id = il.film_id
    WHERE il.eklenme_tarihi >= CURRENT_DATE - INTERVAL '7 days'
    GROUP BY f.id
    ORDER BY COUNT(il.id) DESC
    LIMIT limit_sayisi;
END;
$function$
^^^

-- 4) Takip edilen kullanicilarin son 7 gunde izleme listesine ekledikleri
CREATE OR REPLACE FUNCTION arkadaslarin_bu_hafta_izledikleri(kullanici_id_girdi bigint, limit_sayisi integer)
RETURNS SETOF filmler
LANGUAGE plpgsql
AS $function$
BEGIN
    RETURN QUERY
    SELECT f.*
    FROM filmler f
    JOIN izleme_listesi il ON f.id = il.film_id
    JOIN takipler t ON t.takip_edilen_id = il.kullanici_id
    WHERE t.takip_eden_id = kullanici_id_girdi
      AND il.eklenme_tarihi >= CURRENT_DATE - INTERVAL '7 days'
    GROUP BY f.id
    ORDER BY COUNT(il.id) DESC
    LIMIT limit_sayisi;
END;
$function$
^^^

-- 5) Yardimci prosedurler (uygulama disindan, psql ile kullanilir)
CREATE OR REPLACE PROCEDURE film_sil(IN silinecek_film_id bigint)
LANGUAGE plpgsql
AS $procedure$
BEGIN
    DELETE FROM izleme_listesi WHERE film_id = silinecek_film_id;
    DELETE FROM degerlendirmeler WHERE film_id = silinecek_film_id;
    DELETE FROM filmler WHERE id = silinecek_film_id;
    RAISE NOTICE 'Film (ID: %) ve tum iliskili verileri silindi.', silinecek_film_id;
END;
$procedure$
^^^

CREATE OR REPLACE PROCEDURE izleme_listesini_temizle(IN p_kullanici_id bigint)
LANGUAGE plpgsql
AS $procedure$
BEGIN
    DELETE FROM izleme_listesi WHERE kullanici_id = p_kullanici_id;
    RAISE NOTICE '% IDli kullanicinin listesi temizlendi.', p_kullanici_id;
END;
$procedure$
^^^

-- Not: Yeni sifre duz metin yazilirsa uygulama ilk basarili giriste otomatik BCrypt'e cevirir.
CREATE OR REPLACE PROCEDURE kullanici_sifre_sifirla(IN p_email character varying, IN p_yeni_sifre character varying)
LANGUAGE plpgsql
AS $procedure$
BEGIN
    UPDATE kullanicilar
    SET sifre = p_yeni_sifre
    WHERE email = p_email;

    IF NOT FOUND THEN
        RAISE NOTICE 'Bu email ile kayitli kullanici bulunamadi: %', p_email;
    END IF;
END;
$procedure$
^^^
