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
 * Hotel guardado como favorito por un usuario.
 * Se guarda el hotel (no la unidad) y la pareja usuario + hotel es unica.
 */
@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(name = "uk_fav_user_hotel",
                columnNames = {"favUserSec", "favHotSec"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favSec")
    private Integer favSec;

    // ON DELETE CASCADE: borrar el usuario o el hotel borra sus favoritos (no bloquea el borrado).
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "favUserSec", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "favHotSec", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Hotel hotel;

    @Column(name = "favCreatedAt")
    private LocalDateTime favCreatedAt;

    @PrePersist
    void onCreate() {
        if (favCreatedAt == null) {
            favCreatedAt = LocalDateTime.now();
        }
    }
}
