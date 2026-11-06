# Plan — Favoritos de hoteles (backend)

Un usuario con sesión guarda hoteles como favoritos y los ve después.
Plan del frontend: `../SweetNestUI/SweetNestUI/docs/planes/completados/favoritos-frontend.md`.

## Estado

| Fase | Estado | Contenido |
|---|---|---|
| 1 | ✅ 2026-11-03 | Tabla `favorites`, servicio, endpoints `/api/favorites`, seed, tests (61/61) |

Detalle → `docs/BITACORA.md` (2026-11-03).

---

## Decisiones

- **Llave:** PK propia `favSec` (IDENTITY, igual que `resSec`/`uniSec`) **+** `UNIQUE (favUserSec, favHotSec)`.
  El id solo no evita duplicados; la restricción única sí.
- Se guarda el **hotel**, no la unidad (la unidad es solo para reservar).
- `PUT` y `DELETE /api/favorites/{hotSec}` son **idempotentes**: no hace falta código de error nuevo
  y no salta el `409/1017` genérico de `DataIntegrityViolationException`.
- Hotel inexistente → `404/1007`; inactivo → `409/1016`. Sin token → `401` (`anyRequest().authenticated()`).
- `ON DELETE CASCADE` en las dos FK: borrar un hotel o un usuario no queda bloqueado por favoritos.
- `GET /api/favorites/ids` existe para que el front pinte los corazones sin una llamada por card.
- El hotel de la respuesta usa el formato de lista (`HotelDto`); el armado se movió de
  `HotelService.mapToDto` a `HotelMapper.toListDto` para reutilizarlo.

## Fase 1

- [x] `entity/Favorite.java` (tabla `favorites`, prefijo `fav`)
- [x] `repository/FavoriteRepository.java`
- [x] `service/IFavoriteService.java` + `service/impl/FavoriteService.java`
- [x] `dto/response/FavoriteResponse.java` + `mapper/FavoriteMapper.java`
- [x] `controller/FavoriteController.java`
- [x] Seed: 2 favoritos para `usuario@sweetnest.com` (después de reservas)
- [x] `FavoritesAcceptanceTests` (7 tests) · `mvn test` 61/61
- [x] `API.md` sección 18 · regla 14 en `CLAUDE.md`
