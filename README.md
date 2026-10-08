# FilmBox: Film Puanlama ve Sosyal Keşif Platformu

FilmBox, kullanıcıların filmleri puanlayıp yorumlayabildiği, izleme listesi oluşturabildiği ve arkadaşlarını takip ederek **ne izlendiğini keşfedebildiği** tam yığın (full-stack) bir web uygulamasıdır. Backend **Spring Boot + PostgreSQL**, arayüz **React** ile geliştirilmiştir. Trend ve öneri listeleri, veritabanı tarafında yazılmış **PostgreSQL fonksiyonları** ile üretilir.

## Özellikler

**Kullanıcılar**
- Kayıt olma ve giriş yapma (`USER` / `ADMIN` rolleri)
- Üst üste 3 hatalı girişte arayüzde geçici kilitleme (10 saniye)
- Oturumun tarayıcıda saklanması (sayfa yenilense de giriş korunur)

**Filmler ve değerlendirmeler**
- Afiş kartlarıyla film listesi ve ada göre anlık arama
- Film detay sayfası: konu, yönetmen, oyuncular, tür, yapım yılı, ortalama puan
- 1–10 arası puan ve yorum yazma, kendi yorumunu silme
- Kişisel **izleme listesine** film ekleme

**Sosyal özellikler**
- Kullanıcı profilleri: izlenen filmler, takipçiler ve takip edilenler sekmeleri
- Kullanıcı takip etme / takibi bırakma

**Keşfet ekranı**
- 🔥 Haftanın trend filmleri
- 🏆 Tüm zamanların en iyileri
- 👥 Arkadaşların bu hafta izledikleri

**Yönetim paneli (ADMIN)**
- Film ekleme, düzenleme ve silme
- Herhangi bir yorumu silme

## Mimari

```
React (film-frontend, :3000)  ──REST/JSON──►  Spring Boot API (:8080)  ──JPA/Hibernate──►  PostgreSQL
                                                  │                                            │
                                      controller / repository / model              fonksiyonlar + trigger
```

```
film_puanlama/
├── src/main/java/com/example/filmpuanlama/
│   ├── controller/    Film, Kullanici, Degerlendirme, IzlemeListesi, Takip
│   ├── model/         Film, Kullanici, Degerlendirme, IzlemeListesi, Takip (JPA varlıkları)
│   ├── repository/    Spring Data JPA depoları (Keşfet için native sorgular dahil)
│   └── config/        VeriYukleyici (ilk çalıştırmada örnek veri)
├── src/main/resources/application.properties
└── film-frontend/     React arayüzü
```

### Veri Modeli

| Tablo | Açıklama |
|---|---|
| `filmler` | Film bilgileri ve ortalama puan |
| `kullanicilar` | Kullanıcılar ve roller |
| `degerlendirmeler` | Kullanıcı–film puan ve yorumları (çoktan-çoğa ilişki) |
| `izleme_listesi` | Kullanıcı–film izleme listesi kayıtları |
| `takipler` | Kullanıcılar arası takip ilişkisi |

### REST API

| Kaynak | Uç noktalar |
|---|---|
| Filmler `/api/filmler` | `GET` listele · `POST` ekle · `PUT /{id}` güncelle · `DELETE /{id}` sil · `GET /populer` · `GET /trendler` · `GET /arkadas-onerileri/{kullaniciId}` |
| Değerlendirmeler `/api/degerlendirmeler` | `GET /film/{filmId}` · `POST` yorum ekle · `DELETE /{id}` |
| İzleme listesi `/api/izleme-listesi` | `GET /{kullaniciId}` · `POST /ekle` · `POST /cikar` |
| Takip `/api/takip` | `POST /et` · `POST /birak` · `GET /ettikleri/{userId}` · `GET /takipcileri/{userId}` |
| Kullanıcılar `/api/kullanicilar` | `POST /giris` · `POST /kayit` |

### Veritabanı Nesneleri

Keşfet ekranı ve ortalama puan hesabı PostgreSQL tarafındaki nesnelere dayanır:

| Nesne | Görevi |
|---|---|
| `en_cok_izlenenleri_getir(limit)` | Tüm zamanların en iyi filmleri |
| `bu_hafta_en_cok_izlenenleri_getir(limit)` | Haftanın trend filmleri |
| `arkadaslarin_bu_hafta_izledikleri(kullanici_id, limit)` | Takip edilen kullanıcıların bu hafta izledikleri |
| Trigger | Yorum eklendiğinde/silindiğinde filmin ortalama puanını günceller |

> Bu nesnelerin SQL tanımları depoya eklenmelidir; kurulumdan önce veritabanında oluşturulmaları gerekir.

## Kullanılan Teknolojiler

- **Backend:** Java 17, Spring Boot 4, Spring Web MVC, Spring Data JPA (Hibernate), PostgreSQL, Maven
- **Frontend:** React 19, Create React App, Fetch API, CSS
- **Veritabanı:** PostgreSQL (saklı fonksiyonlar, trigger, native sorgular)

## Kurulum ve Çalıştırma

### Gereksinimler
- JDK 17+
- Node.js 18+ ve npm
- PostgreSQL

### 1. Veritabanı

```sql
CREATE DATABASE film_puanlama_db;
```

Tablolar uygulama ilk çalıştığında Hibernate (`ddl-auto=update`) tarafından oluşturulur. Veritabanı şifresi ortam değişkeninden okunur:

```bash
export DB_PASSWORD=postgres_sifreniz        # Windows PowerShell: $env:DB_PASSWORD="..."
```

### 2. Backend

```bash
git clone https://github.com/MelihAliCagman/film_puanlama.git
cd film_puanlama
./mvnw spring-boot:run                      # Windows: mvnw.cmd spring-boot:run
```

API `http://localhost:8080` adresinde çalışır. Film tablosu boşsa uygulama, örnek bir film, kullanıcı ve yorum ekler.

### 3. Frontend

```bash
cd film-frontend
npm install
npm start
```

Arayüz `http://localhost:3000` adresinde açılır.

### Yönetici (ADMIN) hesabı

Kayıt ekranı varsayılan olarak `USER` rolü verir. Bir hesabı yönetici yapmak için:

```sql
UPDATE kullanicilar SET rol = 'ADMIN' WHERE email = 'hesabinizin@emaili.com';
```

## Yol Haritası

- Parolaların BCrypt ile hashlenmesi ve Spring Security + JWT ile yetkilendirme
- Veritabanı fonksiyon ve trigger betiklerinin sürüm kontrolüne eklenmesi
- API adresinin ortam değişkenine taşınması
- Sayfalama ve film posteri yükleme
- Docker Compose ile tek komutla kurulum
