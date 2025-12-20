import React, { useEffect, useState } from 'react';
import './App.css';

function App() {
    // --- TEMEL STATE'LER ---
    const [oturum, setOturum] = useState(null);
    const [girisForm, setGirisForm] = useState({ email: "", sifre: "" });
    const [kayitForm, setKayitForm] = useState({ adSoyad: "", email: "", sifre: "", rol: "USER" });
    const [ekran, setEkran] = useState("giris");

    // GÜVENLİK STATE'LERİ
    const [hataMesaji, setHataMesaji] = useState("");
    const [hataliGirisSayisi, setHataliGirisSayisi] = useState(0);
    const [kilitli, setKilitli] = useState(false);
    const [kalanSure, setKalanSure] = useState(0);

    // FİLM & LİSTE STATE'LERİ
    const [filmler, setFilmler] = useState([]);
    const [seciliFilm, setSeciliFilm] = useState(null);
    const [yorumlar, setYorumlar] = useState([]);
    const [yeniYorum, setYeniYorum] = useState({ puan: "", yorum: "" });
    const [izlemeListesi, setIzlemeListesi] = useState([]);
    const [listeModu, setListeModu] = useState(false);

    // PROFİL STATE'LERİ
    const [profilModu, setProfilModu] = useState(false);
    const [bakilanKullanici, setBakilanKullanici] = useState(null);
    const [bakilanListe, setBakilanListe] = useState([]);
    const [bakilanTakipciler, setBakilanTakipciler] = useState([]);
    const [bakilanTakipEdilenler, setBakilanTakipEdilenler] = useState([]);
    const [aktifSekme, setAktifSekme] = useState("liste");

    // ADMIN STATE'LERİ
    const [adminModu, setAdminModu] = useState(false);
    const [yeniFilm, setYeniFilm] = useState({
        ad: "", yonetmen: "", oyuncular: "", konu: "",
        yapimYili: "", tur: "", posterUrl: ""
    });

    // --- YENİ: GÜNCELLEME STATE'LERİ ---
    const [duzenlemeModu, setDuzenlemeModu] = useState(false);
    const [duzenlenecekId, setDuzenlenecekId] = useState(null);

    // KEŞFET STATE'LERİ
    const [kesfetModu, setKesfetModu] = useState(false);
    const [populerFilmler, setPopulerFilmler] = useState([]);
    const [trendFilmler, setTrendFilmler] = useState([]);
    const [arkadasFilmleri, setArkadasFilmleri] = useState([]);

    const [arama, setArama] = useState("");

    // ==============================
    //       BAŞLANGIÇ (Load)
    // ==============================
    useEffect(() => {
        const kayitliOturum = localStorage.getItem("oturum");
        if (kayitliOturum) {
            const user = JSON.parse(kayitliOturum);
            setOturum(user);
            tumFilmleriGetir();
            izlemeListesiniGetir(user.id, true);
        }
    }, []);

    // Timer Mantığı
    useEffect(() => {
        let timer;
        if (kilitli && kalanSure > 0) {
            timer = setTimeout(() => {
                setKalanSure(kalanSure - 1);
            }, 1000);
        } else if (kalanSure === 0 && kilitli) {
            setKilitli(false); setHataliGirisSayisi(0); setHataMesaji("");
        }
        return () => clearTimeout(timer);
    }, [kilitli, kalanSure]);

    // ==============================
    //       API FONKSİYONLARI
    // ==============================

    const girisYap = () => {
        if (kilitli) return;
        fetch('http://localhost:8080/api/kullanicilar/giris', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(girisForm)
        })
            .then(res => { if (!res.ok) throw new Error("Ağ"); return res.text(); })
            .then(text => {
                const data = text ? JSON.parse(text) : null;
                if (data && data.id) {
                    setOturum(data);
                    localStorage.setItem("oturum", JSON.stringify(data));
                    setHataMesaji(""); setHataliGirisSayisi(0);
                    tumFilmleriGetir(); izlemeListesiniGetir(data.id, true);
                } else {
                    const yeniSayi = hataliGirisSayisi + 1;
                    setHataliGirisSayisi(yeniSayi);
                    if (yeniSayi >= 3) { setKilitli(true); setKalanSure(10); setHataMesaji("Çok fazla hatalı deneme!"); }
                    else { setHataMesaji(`Kullanıcı adı veya şifre hatalı! (${yeniSayi}/3)`); }
                }
            }).catch(() => setHataMesaji("Sunucuya bağlanılamadı!"));
    };

    const cikisYap = () => {
        setOturum(null); localStorage.removeItem("oturum");
        setSeciliFilm(null); setListeModu(false); setAdminModu(false); setProfilModu(false); setKesfetModu(false);
    };

    const kayitOl = () => {
        fetch('http://localhost:8080/api/kullanicilar/kayit', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(kayitForm)
        }).then(() => { alert("Kayıt Başarılı!"); setEkran("giris"); });
    };

    const tumFilmleriGetir = () => {
        fetch('http://localhost:8080/api/filmler').then(res => res.json()).then(data => setFilmler(data));
    };

    const izlemeListesiniGetir = (kullaniciId, benimMi) => {
        fetch(`http://localhost:8080/api/izleme-listesi/${kullaniciId}`).then(res => res.json()).then(data => {
            if(benimMi) setIzlemeListesi(data); else setBakilanListe(data);
        });
    };

    // --- ADMIN İŞLEMLERİ (YENİLENEN KISIM) ---

    // Film Ekle
    const filmEkle = () => {
        fetch('http://localhost:8080/api/filmler', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(yeniFilm)
        }).then(() => { alert("Film Eklendi!"); formTemizle(); tumFilmleriGetir(); });
    };

    // Film Sil
    const filmSil = (e, id) => {
        e.stopPropagation();
        if(window.confirm("Silinsin mi?")) {
            fetch(`http://localhost:8080/api/filmler/${id}`, { method: 'DELETE' }).then(() => { alert("Silindi"); tumFilmleriGetir(); });
        }
    };

    // Düzenleme Modunu Başlat
    const filmDuzenlemeyiBaslat = (film) => {
        setYeniFilm({
            ad: film.ad, yonetmen: film.yonetmen, oyuncular: film.oyuncular || "",
            konu: film.konu, yapimYili: film.yapimYili, tur: film.tur, posterUrl: film.posterUrl
        });
        setDuzenlenecekId(film.id);
        setDuzenlemeModu(true);
        window.scrollTo(0, 0); // Formun olduğu yere çık
    };

    // Formu Sıfırla
    const formTemizle = () => {
        setYeniFilm({ ad: "", yonetmen: "", oyuncular: "", konu: "", yapimYili: "", tur: "", posterUrl: "" });
        setDuzenlemeModu(false);
        setDuzenlenecekId(null);
    };

    // Güncelleme İsteği (PUT)
    const filmGuncelle = () => {
        fetch(`http://localhost:8080/api/filmler/${duzenlenecekId}`, {
            method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(yeniFilm)
        }).then(() => { alert("Film Güncellendi!"); formTemizle(); tumFilmleriGetir(); });
    };

    // ------------------------------------------

    const kesfetVerileriniGetir = () => {
        fetch('http://localhost:8080/api/filmler/populer').then(res=>res.json()).then(data=>setPopulerFilmler(data));
        fetch('http://localhost:8080/api/filmler/trendler').then(res=>res.json()).then(data=>setTrendFilmler(data));
        if(oturum) fetch(`http://localhost:8080/api/filmler/arkadas-onerileri/${oturum.id}`).then(res=>res.json()).then(data=>setArkadasFilmleri(data));
    };

    const kesfeteGit = () => {
        setKesfetModu(true); setListeModu(false); setAdminModu(false); setProfilModu(false); setSeciliFilm(null);
        kesfetVerileriniGetir();
    };

    const profileGit = (kullanici) => {
        setBakilanKullanici(kullanici); setProfilModu(true); setSeciliFilm(null); setListeModu(false); setAdminModu(false); setKesfetModu(false);
        setAktifSekme("liste");
        fetch(`http://localhost:8080/api/izleme-listesi/${kullanici.id}`).then(res => res.json()).then(data => setBakilanListe(data));
        fetch(`http://localhost:8080/api/takip/takipcileri/${kullanici.id}`).then(res => res.json()).then(data => setBakilanTakipciler(data));
        fetch(`http://localhost:8080/api/takip/ettikleri/${kullanici.id}`).then(res => res.json()).then(data => setBakilanTakipEdilenler(data));
    };

    const takipEt = () => {
        fetch('http://localhost:8080/api/takip/et', {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ edenId: oturum.id, edilenId: bakilanKullanici.id })
        }).then(res => res.text()).then(msg => { alert(msg); profileGit(bakilanKullanici); });
    };

    const filmSec = (film) => {
        setSeciliFilm(film); setListeModu(false); setProfilModu(false); setKesfetModu(false);
        fetch(`http://localhost:8080/api/degerlendirmeler/film/${film.id}`).then(res => res.json()).then(data => setYorumlar(data));
    };

    const listeyeEkle = () => {
        fetch('http://localhost:8080/api/izleme-listesi/ekle', {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ kullaniciId: oturum.id, filmId: seciliFilm.id })
        }).then(res => res.text()).then(mesaj => { alert(mesaj); izlemeListesiniGetir(oturum.id, true); });
    };

    const yorumGonder = () => {
        if (!yeniYorum.puan) return alert("Puan ver!");
        fetch('http://localhost:8080/api/degerlendirmeler', {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ filmId: seciliFilm.id, kullaniciId: oturum.id, puan: yeniYorum.puan, yorum: yeniYorum.yorum })
        }).then(res => { if(res.ok) { filmSec(seciliFilm); setYeniYorum({ puan: "", yorum: "" }); tumFilmleriGetir(); } });
    };

    const yorumSil = (yorumId) => {
        if(!window.confirm("Silinsin mi?")) return;
        fetch(`http://localhost:8080/api/degerlendirmeler/${yorumId}`, { method: 'DELETE' }).then(() => { alert("Silindi."); filmSec(seciliFilm); tumFilmleriGetir(); });
    };

    const filtrelenmisFilmler = filmler.filter(film => film.ad.toLowerCase().includes(arama.toLowerCase()));

    // ==============================
    //       EKRAN TASARIMI
    // ==============================

    if (!oturum) return (
        <div className="App auth-container">
            <div className="auth-box">
                <h1>🎬 FilmBox</h1>
                {hataMesaji && <div className={`error-box ${kilitli ? 'locked' : ''}`}>{hataMesaji} {kilitli && <span className="timer">{kalanSure}sn</span>}</div>}
                {ekran === "giris" ? (
                    <>
                        <h2>Giriş</h2>
                        <input placeholder="Email" disabled={kilitli} onChange={e=>setGirisForm({...girisForm,email:e.target.value})}/>
                        <input type="password" placeholder="Şifre" disabled={kilitli} onChange={e=>setGirisForm({...girisForm,sifre:e.target.value})}/>
                        <button onClick={girisYap} disabled={kilitli} className={kilitli ? "disabled-btn" : ""}>{kilitli ? `Bekleyin (${kalanSure})` : "Giriş"}</button>
                        <p onClick={()=>!kilitli && setEkran("kayit")} className="link">Kayıt Ol</p>
                    </>
                ) : (
                    <>
                        <h2>Kayıt</h2>
                        <input placeholder="Ad Soyad" onChange={e=>setKayitForm({...kayitForm,adSoyad:e.target.value})}/>
                        <input placeholder="Email" onChange={e=>setKayitForm({...kayitForm,email:e.target.value})}/>
                        <input placeholder="Şifre" onChange={e=>setKayitForm({...kayitForm,sifre:e.target.value})}/>
                        <button onClick={kayitOl}>Kayıt</button>
                        <p onClick={()=>setEkran("giris")} className="link">Giriş Yap</p>
                    </>
                )}
            </div>
        </div>
    );

    return (
        <div className="App">
            <header className="app-header">
                <div className="logo" onClick={() => {setSeciliFilm(null); setListeModu(false); setAdminModu(false); setProfilModu(false); setKesfetModu(false);}}>🎬 FilmBox</div>
                {!seciliFilm && !listeModu && !adminModu && !profilModu && !kesfetModu && (
                    <input type="text" className="search-input" placeholder="Film Ara..." value={arama} onChange={(e) => setArama(e.target.value)}/>
                )}
                <div className="user-info">
                    {oturum.rol === "ADMIN" && <button className="admin-btn" onClick={() => {setAdminModu(true); setSeciliFilm(null); setListeModu(false); setProfilModu(false); setKesfetModu(false);}}>🔧 Yönetim</button>}
                    <button className="list-btn" onClick={kesfeteGit}>🔥 Keşfet</button>
                    <button className="list-btn" onClick={() => profileGit(oturum)}>👤 Profilim</button>
                    <button onClick={cikisYap} className="logout-btn">Çıkış</button>
                </div>
            </header>

            <div className="main-content">

                {/* --- 1. ADMIN PANELİ (GÜNCELLENMİŞ) --- */}
                {adminModu ? (
                    <div className="admin-container">
                        <div className="admin-panel">
                            <div className="admin-header">
                                <h2>{duzenlemeModu ? "✏️ Filmi Düzenle" : "➕ Yeni Film Ekle"}</h2>
                                {duzenlemeModu && <button onClick={formTemizle} className="delete-badge" style={{position:'relative', float:'right'}}>İptal Et</button>}
                            </div>
                            <div className="film-form">
                                <div className="form-row">
                                    <div className="form-group"><label>Film Adı</label><input placeholder="Örn: Inception" value={yeniFilm.ad} onChange={e => setYeniFilm({...yeniFilm, ad: e.target.value})} /></div>
                                    <div className="form-group"><label>Yönetmen</label><input placeholder="Örn: Christopher Nolan" value={yeniFilm.yonetmen} onChange={e => setYeniFilm({...yeniFilm, yonetmen: e.target.value})} /></div>
                                </div>
                                <div className="form-group"><label>Oyuncular</label><input placeholder="Örn: Leonardo DiCaprio..." value={yeniFilm.oyuncular || ""} onChange={e => setYeniFilm({...yeniFilm, oyuncular: e.target.value})} /></div>
                                <div className="form-row">
                                    <div className="form-group"><label>Yapım Yılı</label><input type="number" placeholder="2010" value={yeniFilm.yapimYili} onChange={e => setYeniFilm({...yeniFilm, yapimYili: e.target.value})} /></div>
                                    <div className="form-group"><label>Tür</label><input placeholder="Bilim Kurgu..." value={yeniFilm.tur} onChange={e => setYeniFilm({...yeniFilm, tur: e.target.value})} /></div>
                                </div>
                                <div className="form-group"><label>Poster URL</label><input placeholder="https://..." value={yeniFilm.posterUrl} onChange={e => setYeniFilm({...yeniFilm, posterUrl: e.target.value})} /></div>
                                <div className="form-group"><label>Konu</label><textarea rows="4" placeholder="Konu..." value={yeniFilm.konu} onChange={e => setYeniFilm({...yeniFilm, konu: e.target.value})} /></div>
                                {duzenlemeModu ? (
                                    <button onClick={filmGuncelle} className="save-btn" style={{backgroundColor: '#e50914'}}>🔄 Güncellemeyi Kaydet</button>
                                ) : (
                                    <button onClick={filmEkle} className="save-btn">💾 Yeni Film Ekle</button>
                                )}
                            </div>

                            <h3 style={{marginTop:40, borderBottom:'1px solid #333', paddingBottom:10}}>Düzenlemek İçin Film Seç</h3>
                            <div className="film-grid">
                                {filtrelenmisFilmler.map(film => (
                                    <div key={film.id} className="film-card" onClick={() => filmDuzenlemeyiBaslat(film)}>
                                        <button className="delete-badge" onClick={(e) => filmSil(e, film.id)}>🗑️ Sil</button>
                                        <div className="rating-badge" style={{left: 40, background: '#2ecc71'}}>✏️ Düzenle</div>
                                        <img src={film.posterUrl} alt={film.ad} />
                                        <h4>{film.ad}</h4>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>

                    /* --- 2. PROFİL --- */
                ) : profilModu && bakilanKullanici ? (
                    <div>
                        <div className="profile-header">
                            <div><h1>{bakilanKullanici.adSoyad}</h1><div className="profile-stats">{bakilanListe.length} Film izledi • {bakilanTakipciler.length} Takipçi • {bakilanTakipEdilenler.length} Takip</div></div>
                            {bakilanKullanici.id !== oturum.id && <button className="add-list-btn" onClick={takipEt}>Takip Et</button>}
                        </div>
                        <div className="tabs">
                            <button className={aktifSekme === 'liste' ? 'active-tab' : ''} onClick={() => setAktifSekme('liste')}>🎬 İzlediği Filmler</button>
                            <button className={aktifSekme === 'takipci' ? 'active-tab' : ''} onClick={() => setAktifSekme('takipci')}>👥 Takipçiler</button>
                            <button className={aktifSekme === 'takip' ? 'active-tab' : ''} onClick={() => setAktifSekme('takip')}>➡️ Takip Edilenler</button>
                        </div>
                        {aktifSekme === 'liste' && <div className="film-grid">{bakilanListe.map(item => (<div key={item.id} className="film-card" onClick={() => filmSec(item.film)}><img src={item.film.posterUrl} alt={item.film.ad} /><h3>{item.film.ad}</h3><span className="rating-badge">⭐ {item.film.puan}</span></div>))}</div>}
                        {aktifSekme === 'takipci' && <div className="user-grid">{bakilanTakipciler.map(t => (<div key={t.id} className="user-card" onClick={() => profileGit(t.takipEden)}>👤 {t.takipEden.adSoyad}</div>))}</div>}
                        {aktifSekme === 'takip' && <div className="user-grid">{bakilanTakipEdilenler.map(t => (<div key={t.id} className="user-card" onClick={() => profileGit(t.takipEdilen)}>👤 {t.takipEdilen.adSoyad}</div>))}</div>}
                    </div>

                    /* --- 3. KEŞFET --- */
                ) : kesfetModu ? (
                    <div className="kesfet-container">
                        <h2>🔥 Haftanın Trendleri</h2>
                        <div className="film-grid scroll-grid">
                            {trendFilmler.length > 0 ? trendFilmler.map(film => (
                                <div key={film.id} className="film-card" onClick={() => filmSec(film)}>
                                    <img src={film.posterUrl} alt={film.ad} /><h4>{film.ad}</h4>
                                </div>
                            )) : <p style={{padding:20}}>Trend yok.</p>}
                        </div>
                        <h2>🏆 Tüm Zamanların En İyileri</h2>
                        <div className="film-grid scroll-grid">
                            {populerFilmler.map(film => (
                                <div key={film.id} className="film-card" onClick={() => filmSec(film)}>
                                    <img src={film.posterUrl} alt={film.ad} /><h4>{film.ad}</h4>
                                </div>
                            ))}
                        </div>
                        {oturum && (
                            <>
                                <h2>👥 Arkadaşların Ne İzliyor?</h2>
                                <div className="film-grid scroll-grid">
                                    {arkadasFilmleri.length > 0 ? arkadasFilmleri.map(film => (
                                        <div key={film.id} className="film-card" onClick={() => filmSec(film)}>
                                            <img src={film.posterUrl} alt={film.ad} /><h4>{film.ad}</h4>
                                        </div>
                                    )) : <p style={{padding:20}}>Arkadaşların sessiz.</p>}
                                </div>
                            </>
                        )}
                    </div>

                    /* --- 4. FİLM LİSTESİ --- */
                ) : !seciliFilm ? (
                    <div className="film-grid">
                        {filtrelenmisFilmler.map(film => (
                            <div key={film.id} className="film-card" onClick={() => filmSec(film)}>
                                {oturum.rol === "ADMIN" && <button className="delete-badge" onClick={(e) => filmSil(e, film.id)}>🗑️ Sil</button>}
                                <img src={film.posterUrl} alt={film.ad} />
                                <h3>{film.ad}</h3><span className="rating-badge">⭐ {film.puan}</span>
                            </div>
                        ))}
                    </div>

                    /* --- 5. DETAY SAYFASI (YÖNETMEN KISMI DÜZELDİ) --- */
                ) : (
                    <div className="film-detail-container">
                        <div className="detail-backdrop" style={{backgroundImage: `url(${seciliFilm.posterUrl})`}}></div>
                        <div className="detail-content">
                            <button className="back-btn" onClick={() => setSeciliFilm(null)}>← Geri Dön</button>
                            <div className="detail-flex">
                                <div className="poster-wrapper"><img src={seciliFilm.posterUrl} alt={seciliFilm.ad} className="main-poster"/></div>
                                <div className="info-wrapper">
                                    <h1 className="movie-title">{seciliFilm.ad} <span className="movie-year">({seciliFilm.yapimYili})</span></h1>
                                    <div className="meta-tags"><span className="meta-tag tur">{seciliFilm.tur}</span><span className="meta-tag puan">⭐ {seciliFilm.puan}/10</span></div>
                                    <p className="movie-plot">{seciliFilm.konu}</p>
                                    <div className="crew-info">
                                        <div className="crew-item"><span className="label">YÖNETMEN</span><span className="value">{seciliFilm.yonetmen}</span></div>
                                        <div className="crew-item"><span className="label">OYUNCULAR</span><span className="value">{seciliFilm.oyuncular || "Bilgi Yok"}</span></div>
                                    </div>
                                    <button className="hero-btn" onClick={listeyeEkle}>+ Listeme Ekle</button>
                                </div>
                            </div>
                            <div className="comments-section">
                                <h3>Değerlendirmeler</h3>
                                <div className="comment-input-area">
                                    <input type="number" placeholder="Puan" value={yeniYorum.puan} onChange={e=>setYeniYorum({...yeniYorum, puan:e.target.value})} className="score-input"/>
                                    <input type="text" placeholder="Yorum yaz..." value={yeniYorum.yorum} onChange={e=>setYeniYorum({...yeniYorum, yorum:e.target.value})} className="text-input"/>
                                    <button onClick={yorumGonder}>Gönder</button>
                                </div>
                                <div className="reviews-grid">
                                    {yorumlar.map(review => (
                                        <div key={review.id} className="review-bubble">
                                            <div className="review-top"><span className="author" onClick={()=>profileGit(review.kullanici)}>{review.kullanici.adSoyad}</span><span className="score">★ {review.puan}</span></div>
                                            <p>{review.yorum}</p>
                                            {(oturum.id===review.kullanici.id || oturum.rol==="ADMIN") && <button className="trash-btn" onClick={()=>yorumSil(review.id)}>🗑️</button>}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default App;