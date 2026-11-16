package com.dh.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * Valoracion (1-5 estrellas) y resena opcional de un hotel (HU #28).
 * Una por usuario + hotel: volver a enviar la actualiza.
 */
@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(name = "uk_rev_user_hotel",
                columnNames = {"revUserSec", "revHotSec"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revSec")
    private Integer revSec;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "revUserSec", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "revHotSec", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Hotel hotel;

    /** 1 = la mas baja, 5 = la mas alta. */
    @Column(name = "revStars", nullable = false)
    private int revStars;

    @Column(name = "revComment", length = 1000)
    private String revComment;

    @Column(name = "revCreatedAt")
    private LocalDateTime revCreatedAt;

    /** Fecha que se muestra como "publicada": cambia al editar la resena. */
    @Column(name = "revUpdatedAt")
    private LocalDateTime revUpdatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (revCreatedAt == null) {
            revCreatedAt = now;
        }
        if (revUpdatedAt == null) {
            revUpdatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        revUpdatedAt = LocalDateTime.now();
    }
}
