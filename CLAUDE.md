# CLAUDE.md — SweetNest (backend)

Arquitectura y reglas **estables** del sistema. **Mantener corto y vigente.**
- El estado de lo que se está construyendo va en los planes (`docs/planes/`), no acá.
- El registro cronológico de lo que se hace va en `docs/BITACORA.md`.
- `AGENTS.md` solo apunta a este archivo; no duplicar contenido.

## Qué es

API REST de alojamientos (DH - Proyecto Final). Spring Boot 4.0.2 · Java 17 · Lombok ·
Spring Security con JWT (jjwt 0.11.5) · JPA/Hibernate · H2 en memoria · springdoc OpenAPI.

- **Frontend aparte:** `../SweetNestUI/SweetNestUI` (React 19 + Vite, puerto 5173, axios).
  CORS ya apunta ahí (`app.cors.origins`).

## Comandos y build

```bash
# JAVA_HOME global apunta a JDK 11 y el proyecto necesita 17+.
cmd //c "set JAVA_HOME=%USERPROFILE%\.jdks\openjdk-20 && mvnw.cmd test"
cmd //c "set JAVA_HOME=%USERPROFILE%\.jdks\openjdk-20 && mvnw.cmd spring-boot:run"
cmd //c "set JAVA_HOME=%USERPROFILE%\.jdks\openjdk-20 && mvnw.cmd package -DskipTests"
```
Usar `mvnw.cmd` vía `cmd //c`, **no** `./mvnw`: el redirect de bash MSYS falla
escribiendo en `target/` (lo mismo pasa con `sed -i`). Detalle en `docs/BITACORA.md`.

## Base de datos

H2 en memoria `SweetNestDB`, `ddl-auto=create-drop` → **no hay migraciones**.
Todo el esquema sale de las entidades y los datos de `config/DataInitializer.java`
(ejecuta en cada arranque; si se rompe el orden de creación, rompe la app).
Orden obligatorio: tipos → hoteles → unidades → reservas → favoritos.

## Arquitectura y convenciones (seguir el estilo existente, no inventar otro)

- **Paquetes:** `entity`, `repository`, `service` (interfaz `IXxxService`) +
  `service/impl`, `dto` (+ `dto/request`, `dto/response`), `mapper`, `controller`,
  `exception`, `specification`, `config`, `common`.
- **Entidades:** Lombok `@Getter @Setter @NoArgsConstructor @AllArgsConstructor`.
  **Prefijo de 3 letras por tabla** en los nombres de columna:
  `hotSec/hotName` (hotel), `citSec` (ciudad), `CatSec` (categoría), `userSec` (usuario),
  `FeaSec`, `uniSec` (unidades), `typSec` (tipos), `resSec` (reservas).
  PK con `@GeneratedValue(IDENTITY)`. Banderas como char `'S'`/`'N'` en BD y `String` en DTO
  (`@Pattern` no valida `Character`).
- **Tablas:** `hotel` (singular), `users`, `profile`, `city`, `Category`, `hotel_types`,
  `hotel_units`, `reservations`, `favorites` (prefijo `fav`).
- **Controllers:** `@RequestMapping("api/<recurso>")` **sin barra inicial**, rutas en
  singular (`api/hotel`, `api/category`). Devuelven `ResponseEntity`.
- **Respuestas:** `dto/response/ApiResponse` con `ApiResponse.success(...)` /
  `ApiResponse.error(...)` y códigos de `common/ApiResponseCode`.
  *Consistencia parcial a propósito:* los endpoints del feature de reservas usan
  `ApiResponse`; los endpoints viejos de hotel/categoría devuelven DTO o `Page` crudos
  porque el frontend los consume así. **No "unificar" sin actualizar SweetNestUI.**
- **Mensajes de respuesta (estándar):** nunca escribir el texto a mano en el controller. Usar
  `messageService.getMessage("<recurso>.<accion>")` (`config/i18n/MessageService`; idioma **fijo `es`** por `spring.web.locale-resolver=fixed`)
  y agregar la clave en los **tres** `src/main/resources/messages*.properties` (base y `_es` en español, `_en` en inglés para cuando se active el idioma por header).
  Claves: `<recurso>.found.all | found | no.found | created | updated | deleted`, o el verbo propio del caso
  (`favorite.added`). Acentos como `\uXXXX` (ver nota de codificación en `docs/BITACORA.md`, 2026-11-03).
  Referencia: `FavoriteController`, `FeatureController`. Pendientes de migrar cuando se toquen:
  `ReservationController`, `HotelTypeController`, `HotelUnitController`.
- **Errores:** excepciones propias en `exception/` + handler en `GlobalExceptionHandler`
  (estilo: `HotelNotFoundException`). Agregar código en `ApiResponseCode`.
- **Seguridad:** todo authenticated por defecto (`anyRequest().authenticated()`);
  lo público se habilita en `config/SecurityConfiguration.java` filtrando por `HttpMethod`.
  Perfil admin = autoridad `Administrador` (`hasAuthority`), sale de `Profile.name`
  en `User.getAuthorities()`. Anfitrión = `users.userHost='S'`. Ver `docs/RBAC.md`.
- **Mappers:** a mano con Lombok/`@Component` (no usa MapStruct), ver `mapper/HotelMapper`.

## Reglas de negocio ya decididas (no volver a preguntar)

1. Solo se reserva la **unidad**; toda reserva → `hotel_units`, nunca → hotel.
2. Todo alojamiento tiene ≥1 unidad (automática si `typMultiUnit='N'`).
3. El día de salida queda libre: cruce = `existe.checkIn < nueva.checkOut && existe.checkOut > nueva.checkIn`.
4. `PENDING` y `CONFIRMED` ocupan unidad; `CANCELLED` y `COMPLETED`, no.
5. `hotConfirmReq='N'` → la reserva nace `CONFIRMED`; `'S'` → nace `PENDING` y la confirma
   el admin **o el anfitrión del hotel**.
6. Reserva siempre nace con precio congelado (`resPrice`) y `resTotal` calculado en backend.
7. Búsqueda y detalle públicos; reservar exige sesión. Usuario sale del **token**, nunca del body.
8. Rutas nuevas siguen la convención existente (`/api/hotel/...`), no se migra a plural.
9. Sin migraciones: respetar el orden de `DataInitializer` (ver arriba).

**Anfitrión** (`hotels.hotUserSec` → `users`):
10. Solo el admin cambia la marca `host` de un usuario (otro → `403/1018`).
11. Para asignar `hostId` a un hotel, el usuario debe existir (`404/1004`) y tener
    `userHost='S'` (`409/1020`). `hostId=null` en update = no cambia el anfitrión.
12. Confirmar: admin siempre · anfitrión del hotel · hotel sin anfitrión + no-admin → `409/1019`
    · cualquier otro → `403/1018`. Cancelar: solo dueño de la reserva o admin.
13. `GET /api/reservations/incoming` = reservas de los hoteles cuyo anfitrión es el usuario
    del token (si no es anfitrión de nada → `200` con lista vacía).

**Favoritos** (`favorites`):
14. Favorito = **usuario + hotel** (no unidad), único por `UNIQUE (favUserSec, favHotSec)`; exige sesión.
    `PUT`/`DELETE /api/favorites/{hotSec}` son idempotentes. Borrar usuario u hotel borra sus favoritos (`ON DELETE CASCADE`).

## Flujo de planes

```
docs/planes/
  pendientes/    # planes aprobados que todavía no se empezaron
  en-curso/      # lo que se está implementando (idealmente uno solo)
  completados/   # cerrados; no se editan más
```
- Plan nuevo → `docs/planes/pendientes/<tema>.md` (nombre corto en kebab-case).
- Al empezar a implementarlo → `git mv` a `en-curso/`. Al cerrar la última fase → `completados/`.
- Cada plan lleva arriba un bloque **Estado** por fase (✅ hecho / ⏳ siguiente / ⬜ pendiente).
- Al cerrar una fase: actualizar el bloque Estado del plan + agregar una entrada en `docs/BITACORA.md`.
- Si se decide una regla nueva que va a durar, se suma a "Reglas de negocio" de este archivo.

**Plan activo:** `docs/planes/en-curso/reservas.md`.

## Docs

- `API.md`: endpoints + curl · `PLAN_DE_PRUEBAS.md`: plan de pruebas backend
- `docs/RBAC.md`: perfiles y permisos · `docs/BITACORA.md`: registro cronológico
- `docs/planes/`: planes por estado (ver arriba)
