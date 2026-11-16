# Plan — HU #28: Puntuar producto (backend)

Plan del frontend: `../SweetNestUI/SweetNestUI/docs/planes/en-curso/resenas.md`.

## Estado

| Fase | Estado | Contenido |
|---|---|---|
| 1 | ✅ 2026-11-12 | `reviews`, `ReviewService` (elegibilidad + upsert + promedio), `api/reviews`, seed, tests. 70/70 |
| 2 | ⏳ | Commit + revisión de punta a punta con el frontend |

Detalle → `docs/BITACORA.md` (2026-11-12). Regla de negocio en `CLAUDE.md`.

## Decisiones

- **Quién puede valorar:** quien tiene una reserva `CONFIRMED` o `COMPLETED` en ese hotel con `resCheckOut <= hoy`.
  `COMPLETED` nunca se asigna, por eso no se depende solo de él.
- **Una reseña por usuario + hotel, editable:** volver a enviarla actualiza las estrellas, el comentario y la fecha.
- El promedio se guarda desnormalizado en `hotels.hotRatingAvg` / `hotRatingCount` y se recalcula en cada guardado.

---

## Fase 1

- [x] `entity/Review` → `reviews` (prefijo `rev`, `UNIQUE (revUserSec, revHotSec)`, `ON DELETE CASCADE`) + `ReviewRepository`
- [x] `ReviewService`: elegibilidad, upsert, recalcula promedio (1 decimal) y conteo
- [x] `ReviewController` `api/reviews`: `GET /hotel/{id}` (público), `GET /hotel/{id}/mine`, `PUT /hotel/{id}`
- [x] `REVIEW_NOT_ALLOWED(1021)` → 403 · mensajes `review.*` en los 3 properties
- [x] `ratingAvg` / `ratingCount` en `HotelResponseDto` y `HotelDto`
- [x] Seed: `viajera@` / `viajero@` con estadías pasadas y 5 reseñas
- [x] `ReviewsAcceptanceTests` (5) · 70/70 (corrido en una copia)

## Fase 2

- [ ] Commit (`feat: valoraciones y reseñas de alojamientos (HU #28)`)
- [ ] Un usuario que puede valorar publica y edita su reseña; uno que no puede recibe 403; el promedio se actualiza en el detalle y en las cards
