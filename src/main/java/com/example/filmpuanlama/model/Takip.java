package com.example.filmpuanlama.model;

import jakarta.persistence.*;
import lombok.Data; // Lombok yoksa getter/setter elle ekle

@Entity
@Table(name = "takipler")
public class Takip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "takip_eden_id")
    private Kullanici takipEden;

    @ManyToOne
    @JoinColumn(name = "takip_edilen_id")
    private Kullanici takipEdilen;

    // --- Getter ve Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Kullanici getTakipEden() { return takipEden; }
    public void setTakipEden(Kullanici takipEden) { this.takipEden = takipEden; }
    public Kullanici getTakipEdilen() { return takipEdilen; }
    public void setTakipEdilen(Kullanici takipEdilen) { this.takipEdilen = takipEdilen; }
}