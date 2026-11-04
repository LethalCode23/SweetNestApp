# Bitácora — Feature de búsqueda y reservas

Registro cronológico de lo que se crea y decide. **Agregar una entrada por sesión/fase,
no reescribir las anteriores.** Convenciones y reglas permanentes → `CLAUDE.md`.

---

## 2026-10-18 — Diseño del feature

**Documento base:** `docs/planes/en-curso/reservas.md` (antes `docs/reservas-feature-plan.md`; analizado contra el código real).

### Hallazgos del análisis

| # | Hallazgo | Acción |
|---|---|---|
| 1 | Las rutas del plan (`/api/hotels/{id}`) no coinciden con las reales (`/api/hotel/findDetailById/{id}`) | Mantener convención actual `api/hotel` |
| 2 | "ApiResponse en todo" rompería SweetNestUI (consume `Page`/DTO crudos) | `ApiResponse` solo en endpoints nuevos |
| 3 | `hotCost` es el filtro de precio de `HotelSpecification:25` y del frontend | No eliminar; sincronizar con `MIN(price_per_night)` (Fase 3) |
| 4 | `JwtAuthenticationFilter:42` lanza excepción con token vencido → 500 en endpoints públicos | Fix con try/catch → Fase 4 |
| 5 | No hay Flyway: `ddl-auto=create-drop` + H2 | Orden crítico en `DataInitializer`; sin migraciones |
| 6 | `HotelService.update()` devuelve `null` (línea 136) | Arreglar en Fase 1 |
| 7 | `HotelDto` usa constructor posicional de 8 args (líneas 71 y 88) | Se toca al agregar campos |
| 8 | El frontend está en `../SweetNestUI/SweetNestUI` (repo aparte) | Fase 6 = repo separado |
| 9 | No hay librería de calendario doble en SweetNestUI | Agregar `@mui/x-date-pickers` en Fase 6 |

### Decisiones cerradas con el usuario

| # | Decisión |
|---|---|
| D1 | **Rutas:** mantener la convención actual (`/api/hotel/...`), no migrar a plural |
| D2 | **Estado inicial:** la reserva nace `PENDING` y la aprueba un administrador |
| D3 | **`ApiResponse`:** solo en los endpoints nuevos |
| D4 | **Nombres de columna:** con prefijo de 3 letras, como el resto del código |
| D5 | **Tipos de alojamiento:** tabla `hotel_types` administrable (no enum) |
| D6 | **Detalle sin fechas:** muestra todas las unidades activas; las fechas son opcionales |
| D7 | **`hotCost`:** se mantiene (nunca eliminar) |
| D8 | **Búsqueda sin fechas:** no aplica predicado de disponibilidad |
| D9 | **Bandera de confirmación:** a nivel de **hotel** (`hotConfirmReq`), no por unidad ni por tipo |
| D10 | La bandera **ya surte efecto en la Fase 1**: `'N'` → nace `CONFIRMED`, `'S'` → `PENDING` |
| D11 | Se edita con el `PUT /api/hotel/update/{id}` existente (sin endpoint nuevo) |
| D12 | **Anfitrión:** se pospone a la Fase 2 (columna `hotUserSec`, bandeja `/incoming`, `HOST_NOT_ASSIGNED`) |

---

## 2026-10-18 — Fase 1 (en curso)

**Objetivo:** las 3 tablas nuevas con su repositorio, service, DTOs, mapper y controller
(`ApiResponse`), las relaciones del plan, la bandera `hotConfirmReq` y los datos sembrados.
Sin lógica de disponibilidad (eso es Fase 2).

### Alcance

**A. Cambios en `hotels`**
- `hotels.hotTypSec` FK obligatoria → `hotel_types` (`Hotel.type`)
- `hotels.hotConfirmReq` char `S`/`N` — `S` = requiere confirmación, `N` = inmediata
- Se propaga a `HotelDto`, `HotelResponseDto`, `HotelMapper`, `HotelService`
- De paso: arreglar `return null` de `HotelService.update()`

**B. `hotel_types`** → `typSec`, `typName`, `typMultiUnit` (S/N), `typState` (A/I)
- Capa completa: entity, repository, DTO, mapper, service (interfaz + impl), controller
- `GET /api/hotel-types` y `GET /{id}` públicos; `POST/PUT/DELETE` admin; todo con `ApiResponse`
- `DELETE` → 409 si tiene hoteles

**C. `hotel_units`** → `uniSec`, `uniHotSec` FK→hotel, `uniName`, `uniCapacity`, `uniPrice`, `uniState`
- `Hotel.units` (`@OneToMany`)
- `GET /api/hotel-unit/hotel/{hotSec}` público; escritura admin; `DELETE` → 409 con reservas
- Guard: si el tipo es `multiUnit='N'`, no se puede crear unidad manual

**D. `reservations`** → `resSec`, `resUserSec` FK→user, `resUniSec` FK→unit,
`resCheckIn`, `resCheckOut`, `resGuests`, `resPrice`, `resTotal`, `resStatus`, `resCreatedAt`
- Enum `ReservationStatus`: `PENDING, CONFIRMED, CANCELLED, COMPLETED`
- `User.reservations` y `HotelUnit.reservations` (`@OneToMany`)
- Endpoints: `POST /` (lee `hotConfirmReq`), `GET /me`, `GET /?status=` (admin),
  `PATCH /{id}/confirm` (admin), `PATCH /{id}/cancel` (dueño o admin)
- Reglas Fase 1: `checkOut > checkIn`, `checkIn` no pasado, `guests ≤ capacidad`,
  unidad y hotel activos, `resTotal` calculado en backend, precio congelado

**E. Seguridad** — públicos: `GET /api/hotel-types/**`, `GET /api/hotel-unit/hotel/**`;
admin: `GET /api/reservations`, `PATCH /api/reservations/*/confirm`.

**F. Códigos nuevos en `ApiResponseCode`:**

| Código | Código enum | HTTP | Cuándo |
|---|---|---|---|
| 1008 | `HOTEL_TYPE_NOT_FOUND` | 404 | tipo inexistente |
| 1009 | `UNIT_NOT_FOUND` | 404 | unidad inexistente |
| 1010 | `RESERVATION_NOT_FOUND` | 404 | reserva inexistente |
| 1011 | `UNIT_UNAVAILABLE` | 409 | *reservado para Fase 2 (cruce de fechas)* |
| 1012 | `INVALID_STATE_TRANSITION` | 409 | confirmar/cancelar en estado inválido |
| 1013 | `GUESTS_EXCEED_CAPACITY` | 400 | huéspedes > capacidad |
| 1014 | `INVALID_DATE_RANGE` | 400 | `checkOut <= checkIn` |
| 1015 | `RESERVATION_IN_PAST` | 400 | `checkIn` en el pasado |
| 1016 | `INACTIVE_RECORD` | 409 | unidad/tipo/hotel inactivo o tipo de una sola unidad |
| 1017 | `DEPENDENT_RECORDS_EXIST` | 409 | borrado con registros dependientes |
| 1018 | `FORBIDDEN_OPERATION` | 403 | no es dueño ni administrador |

> `BusinessRuleException` (base con `code` + `status`) centraliza los 11: un solo
> `@ExceptionHandler` en `GlobalExceptionHandler`. `DataIntegrityViolationException`
> → 409 como red de seguridad por las FK.

**G. `DataInitializer`** — orden por FK: perfiles → tipos (Hotel `N`, Casa `N`,
Apartamento `S`) → usuarios → 10 hoteles (tipo + bandera mixta) → ≥1 unidad por hotel →
reservas de ejemplo (`CONFIRMED` en hoteles `N`, `PENDING` en hoteles `S`).

**H. Criterios de aceptación** — todos verificados por `ReservasPhase1AcceptanceTests`
- [x] `mvn test` verde → **26/26**
- [x] `GET /api/hotel-types` sin token → 200 con `ApiResponse` (y `typName` = los 3 seed)
- [x] Reservar en hotel `hotConfirmReq='N'` → 201 `status=CONFIRMED` (+ `nights`, `totalPrice`)
- [x] Reservar en hotel `hotConfirmReq='S'` → 201 `status=PENDING`
- [x] `PATCH /{id}/confirm` con perfil `Usuario` → 403 · con `Administrador` → 200 `CONFIRMED`
- [x] Reservar sin token → 401 · `checkOut<=checkIn` → 400 (`1014`) · `guests>capacidad` → 400 (`1013`)
- [x] `checkIn` en el pasado → 400 (`1015`) · unidad inexistente → 404 (`1009`)
- [x] `DELETE /api/hotel-unit/{id}` con reservas → 409 (`1017`)
- [x] `DELETE /api/hotel-types/{id}` con hoteles → 409 (`1017`)
- [x] `POST /api/hotel-unit` en tipo `multiUnit='N'` → 409 (`1016`)
- [x] `PUT /api/hotel/update/{id}` con `hotConfirmReq` y `typeId` → 200 con ambos reflejados
- [x] `GET /api/hotel/findDetailById/{id}` expone `typeId` y `confirmReq`
- [x] `GET /api/reservations` con `Usuario` → 403 · `cancel` de reserva ajena → 403 (`1018`)

### Progreso

- [x] Entidades y enum — `HotelType`, `HotelUnit`, `Reservation`, `ReservationStatus`;
      `Hotel` gana `type` (`hotTypSec` NOT NULL), `hotConfirmReq`, `units`;
      `User` gana `reservations`
- [x] Repositorios — `HotelTypeRepository`, `HotelUnitRepository`, `ReservationRepository`
- [x] DTOs — `HotelTypeDto`, `HotelUnitDto`, `CreateReservationRequest`,
      `ReservationResponse`; `HotelDto`/`HotelResponseDto` ganan `typeId`/`typeName`/bandera
- [x] Mappers — `HotelTypeMapper`, `HotelUnitMapper`, `ReservationMapper`; `HotelMapper` actualizado
- [x] Services — `IHotelTypeService`/`HotelTypeService`, `IHotelUnitService`/`HotelUnitService`,
      `IReservationService`/`ReservationService` (con `@Transactional`)
- [x] Controllers — `HotelTypeController`, `HotelUnitController`, `ReservationController`
- [x] Seguridad — GET públicos de types/units, admin para escritura y para
      `GET /api/reservations` + `PATCH /{id}/confirm`; **entry point 401**
- [x] `ApiResponseCode` + `BusinessRuleException` + 11 subclases + handlers
- [x] `DataInitializer` — 3 tipos → 10 hoteles con tipo y bandera mixta → 18 unidades →
      7 reservas (4 `CONFIRMED`, 3 `PENDING`, 1 `CANCELLED`)
- [x] **`mvn test` verde: 26/26** (`ReservasPhase1AcceptanceTests` 25 + smoke 1)

### Hallazgos durante la implementación (bugs reales, corregidos)

| # | Hallazgo | Corrección |
|---|---|---|
| B1 | **`@Pattern` no tiene validador para `Character`** → `UnexpectedTypeException` → 500 en todo `POST /api/hotel-types` y `/api/hotel-unit` | `typMultiUnit`, `typState`, `uniState` pasan a `String` en el DTO; el mapper convierte con `charAt(0)` |
| B2 | Sin mecanismo de autenticación configurado, Spring Security respondía **403** también a los anónimos | `exceptionHandling().authenticationEntryPoint(HttpStatusEntryPoint(UNAUTHORIZED))` → 401 sin sesión, 403 sin permiso |
| B3 | `@WithMockUser` **no llegaba a aplicarse** en este setup: todas las peticiones caían anónimas y devolvían 403 | Los tests usan post-processors `.with(user(...).authorities(...))` en cada petición (funciona y es explícito) |
| B4 | `HotelService.update()` devolvía `null` (línea 136) | Devuelve `mapToDto(updated)` + `@Transactional` |
| B5 | Warning Lombok `@Builder will ignore the initializing expression` al agregar `User.reservations` | `@Builder.Default` (verificado con `javap`: el ctor sin args sí inicializa la lista) |
| B6 | Spring Boot 4 movió `AutoConfigureMockMvc` a `org.springframework.boot.webmvc.test.autoconfigure` y lo sacó del `starter-test` | Nueva dependencia `spring-boot-starter-webmvc-test` (test) en el `pom` |

### Entorno (para no volver a tropezar)

- `JAVA_HOME` global apunta a **JDK 11**; el proyecto necesita **17** → compilar con
  `set JAVA_HOME=%USERPROFILE%\.jdks\openjdk-20` (OpenJDK 20 que compila `--release 17`).
- **Usar `mvnw.cmd` vía `cmd //c`, no `./mvnw`**: el redirect de bash MSYS sobre este
  repo falla con `NoSuchFileException` al escribir en `target/` (cmd/PowerShell sí escriben).

### Endpoints nuevos (Fase 1)

```
GET    /api/hotel-types            público   lista
GET    /api/hotel-types/actives    público   solo activos
GET    /api/hotel-types/{id}       público
POST   /api/hotel-types            admin
PUT    /api/hotel-types/{id}       admin
DELETE /api/hotel-types/{id}       admin     409 si tiene hoteles

GET    /api/hotel-unit/hotel/{id}  público   unidades activas del hotel
GET    /api/hotel-unit/{id}        admin
POST   /api/hotel-unit             admin     409 si el tipo es multiUnit='N'
PUT    /api/hotel-unit/{id}        admin
DELETE /api/hotel-unit/{id}        admin     409 si tiene reservas activas

POST   /api/reservations           sesión    estado según hotConfirmReq; usuario del token
GET    /api/reservations/me        sesión    ?status=&page=&size=
GET    /api/reservations           admin     ?status=&page=&size=
PATCH  /api/reservations/{id}/confirm   admin          PENDING → CONFIRMED
PATCH  /api/reservations/{id}/cancel    dueño o admin  PENDING|CONFIRMED → CANCELLED
```

**Endpoints modificados (impacto en SweetNestUI):**
- `POST /api/hotel/save`, `PUT /api/hotel/update/{id}` → aceptan `typeId` (obligatorio al
  crear) y `hotConfirmReq` opcional. Sigue devolviendo `HotelDto` **sin** `ApiResponse`.
- `GET /api/hotel/findDetailById/{id}` → agrega `typeId`, `typeName`, `confirmReq`.

### Pendiente de considerar en la Fase 6 (frontend)

El formulario de crear/editar hotel de SweetNestUI debe enviar `typeId` y `hotConfirmReq`;
si no lo hace, el `POST /api/hotel/save` responde 404 (`HOTEL_TYPE_NOT_FOUND`).
Poblar el selector con `GET /api/hotel-types/actives`.

---

## 2026-10-25 — Fase 2 Bloque A (anfitrión del hotel)

**Objetivo:** marca de anfitrión en usuarios, asignación del host al hotel,
bandeja `/incoming` y confirmación por el anfitrión. **Sin** disponibilidad ni
lock anti-doble-reserva (eso es el Bloque B de la Fase 2).

### Decisiones cerradas (las abiertas del plan)

| # | Decisión |
|---|---|
| ⓐ | Hosts seed: **2 usuarios nuevos** (`anfitrion@sweetnest.com`, `anfitrion2@sweetnest.com`), perfil `Usuario`, `userHost='S'` |
| ⓑ | **Confirmar:** admin **o anfitrión del hotel** (antes: solo admin) |
| ⓒ | `HOST_NOT_ASSIGNED (1019)` dispara cuando un **no-admin** confirma en un hotel **sin** `hotUserSec` |
| ⓓ | Marca `users.userHost`: char `'S'`/`'N'` en BD, **`String` en DTO** (lección B1: `@Pattern` no valida `Character`) |
| ⓔ | **Solo admin** cambia el campo `host` (guardia en `UserController` → `403/1018`) |
| ⓕ | `/incoming` sin filtro devuelve todos los estados; `?status=` opcional |

### Cambios realizados

| Archivo | Cambio |
|---|---|
| `entity/User.java` | `userHost` (char, default `'N'`) + relación `hotelsHosted` |
| `entity/Hotel.java` | `host` → FK **`hotUserSec`** (nullable = sin anfitrión) |
| `dto/UserDto`, `dto/request/UpdateUserRequest` | campo `host` (`String`, `@Pattern S\|N`) |
| `dto/HotelDto`, `dto/HotelResponseDto`, `mapper/HotelMapper` | `hostId` (lectura y escritura) |
| `common/ApiResponseCode` | `HOST_NOT_ASSIGNED(1019)`, `USER_NOT_HOST(1020)` |
| `exception/` | `HostNotAssignedException`, `UserNotHostException`, `UserNotFoundException` (usa `1004`/404) |
| `service/impl/UserService` | leer/escribir `userHost` en update y en los DTO de respuesta |
| `controller/UserController` | guardia `assertIsAdmin`: solo admin cambia el campo `host` |
| `service/impl/HotelService` | `findHost()`: existencia (404/1004) + marca `userHost='S'` (409/1020) |
| `repository/ReservationRepository` | finders `findByUnitHotelHostUserSec...` para la bandeja |
| `service/impl/ReservationService` | `findIncoming()`; `confirm()` ahora: admin **o** anfitrión; hotel sin host → `1019` |
| `controller/ReservationController` | `GET /api/reservations/incoming` |
| `config/SecurityConfiguration` | confirm: `hasAuthority(Administrador)` → `authenticated()`; **A9**: `/incoming` sin línea (lo cubre `anyRequest().authenticated()`) |
| `config/DataInitializer` | 2 usuarios anfitrión seed + `assignHost()` de los 10 hoteles (5 y 5) |

### Reglas nuevas

1. Para asignar `hostId` a un hotel: el usuario debe **existir** (404/1004) y tener **`userHost='S'`** (409/1020).
2. `hostId=null` en update = **no cambia** el anfitrión actual → *desasignar un host todavía no es posible (pendiente)*.
3. `/incoming` = reservas cuyo `hotel.hotUserSec` = usuario del token; no es host de nada → `200` lista vacía.
4. `confirm`: admin siempre · anfitrión si el hotel tiene host · hotel sin host + no-admin → `409/1019` · otro usuario → `403/1018`.
5. El **cancelar por anfitrión NO se incluyó**: sigue siendo dueño o admin.

### Tests

**`mvn test` verde: 40/40** (25 aceptación Fase 1 + 14 nuevos en `ReservasPhase2AcceptanceTests` + 1 smoke).

### Pendientes

- **Bloque B (Fase 2):** cruce de disponibilidad + lock anti-doble-reserva (activar `1011 UNIT_UNAVAILABLE`).
- Desasignar anfitrión (aceptar `hostId` vacío en update).
- `API.md`: la Fase 2 quedó documentada (sección 17); **los endpoints de la Fase 1 siguen sin estar ahí**.

---

## 2026-10-31 — Sugerencias de búsqueda (autocompletado)

**Plan:** `docs/planes/completados/sugerencias-busqueda.md` (adaptado del plan genérico `/api/products/suggestions`).

### Qué se hizo

| Archivo | Cambio |
|---|---|
| `dto/response/SearchSuggestionDto` | `record (type, id, name)`; `type` = `CITY` / `HOTEL` |
| `repository/CityRepository` | `findSuggestions(q, Pageable)`: JPQL con proyección, `citState='A'` |
| `repository/HotelRepository` | `findSuggestions(q, Pageable)`: JPQL con proyección, `hotState='A'` |
| `service/IHotelService` + `impl/HotelService` | `getSuggestions(q)`: trim, <2 → `[]`, ciudades + hoteles hasta 10 |
| `controller/HotelController` | `GET api/hotel/suggestions?q=` (q opcional) |
| `config/SecurityConfiguration` | `GET /api/hotel/suggestions` → `permitAll` |

### Decisiones

- Se sugieren **ciudades además de hoteles**: el buscador filtra por `citName`.
- `q` ausente o corto → `200 []`, nunca `400`/`500`.

### Tests

**`mvn test` verde: 46/46** (+6 en `SearchSuggestionsTests`).

### Pendientes

- Frontend (`SweetNestUI`): componente de autocompletado en `HotelFilters.jsx` con debounce.

---

## 2026-11-01 — Calendario público: días no disponibles por hotel

**Pedido:** endpoint público (sin login) que reciba el id del hotel y devuelva los días
ocupados (`PENDING` + `CONFIRMED`) para marcarlos en el calendario del frontend.

### Qué se hizo

| Archivo | Cambio |
|---|---|
| `repository/ReservationRepository` | `findActiveRangesInHotel(hotSec, from, to, occupying)`: proyección `[uniSec, checkIn, checkOut]`, solo unidades activas, cruce `checkIn < to && checkOut > from` |
| `dto/response/HotelUnavailableDatesResponse` | `hotSec`, `from`, `to`, `totalUnits`, `unavailableDates` |
| `service/IReservationService` + `impl/ReservationService` | `findUnavailableDates(hotSec, from, to)`; constante `OCCUPYING` |
| `controller/HotelController` | `GET api/hotel/{hotSec}/unavailable-dates?from=&to=` con `ApiResponse` |
| `config/SecurityConfiguration` | `GET /api/hotel/*/unavailable-dates` → `permitAll` |

### Decisiones

- **Día no disponible = ninguna unidad activa libre ese día.** En casa/apto (1 unidad) es
  "día con reserva"; en multi-unidad solo se bloquea si todas están ocupadas.
- `from` default hoy, `to` exclusivo default `from + 6 meses`, máximo 366 días (`400/INVALID_DATE_RANGE`).
- Solo se exponen fechas (no usuario, precio ni id de reserva) porque es público.
- Hotel sin unidades activas → `totalUnits: 0`, lista vacía.

### Tests

**`mvn test` verde: 54/54** (+8 en `HotelUnavailableDatesTests`).

### Pendientes

- Token vencido en endpoint público sigue fallando en `JwtAuthenticationFilter` (hallazgo #4).
- Frontend: consumir el endpoint y pasar `unavailableDates` al datepicker.

---

## 2026-11-03 — Favoritos de hoteles

Plan: `docs/planes/completados/favoritos.md`.

- Tabla `favorites` (`favSec` PK + `UNIQUE (favUserSec, favHotSec)`, `favCreatedAt`, `ON DELETE CASCADE` en las dos FK).
- `GET /api/favorites`, `GET /api/favorites/ids`, `PUT|DELETE /api/favorites/{hotSec}` (idempotentes, `ApiResponse`).
  Errores reutilizados: `404/1007` hotel inexistente, `409/1016` hotel inactivo. Sin cambios en `SecurityConfiguration`.
- Refactor: `HotelService.mapToDto` → `HotelMapper.toListDto` (mismo resultado; lo reutiliza `FavoriteMapper`).
- Seed: 2 favoritos para `usuario@sweetnest.com`.
- **`mvn test` verde: 61/61** (+7 en `FavoritesAcceptanceTests`).

### Mensajes con `MessageService` (estándar)

- `FavoriteController` ya no tiene textos fijos: usa `messageService.getMessage(...)` con las claves
  `favorite.found.all`, `favorite.ids.found`, `favorite.added` y `favorite.removed` en los tres `messages*.properties`.
- Queda como convención en `CLAUDE.md` ("Mensajes de respuesta"). Los tests de favoritos comprueban el mensaje del PUT y del DELETE. El idioma está fijo en `es` (`spring.web.locale-resolver=fixed`), así que `_en` hoy no se usa.
- **Hallazgo pendiente:** `MessageConfig` lee los `.properties` como UTF-8, pero `messages.properties` y
  `messages_es.properties` están guardados en ISO-8859-1, así que los mensajes con tilde que ya existían
  ("registrado con éxito", "inició sesión", "Módulo") salen con caracteres rotos. Arreglo: convertir esos dos
  archivos a UTF-8. Mientras tanto, los acentos de claves nuevas van como `\uXXXX`.
