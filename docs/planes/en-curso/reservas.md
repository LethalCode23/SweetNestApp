# Feature: búsqueda por disponibilidad y reservas

Plan de implementación del sprint. Documento de diseño, sin código.

## Estado

> Las fases de la implementación real se reorganizaron respecto de la sección 6 (diseño
> original). Este bloque es el que vale; el detalle de cada fase está en `docs/BITACORA.md`.

| Fase | Estado | Contenido |
|---|---|---|
| 1 | ✅ 2026-10-18 | Tablas `hotel_types`, `hotel_units`, `reservations`; `type_id` y `hotConfirmReq` en hoteles; repos, services, DTOs, mappers, controllers con `ApiResponse`. 26/26 tests |
| 2 · Bloque A | ✅ 2026-10-25 | Anfitrión: `users.userHost`, FK `hotels.hotUserSec`, `GET /api/reservations/incoming`, confirmar por admin o anfitrión (`1019`, `1020`). 40/40 tests |
| Extra | ✅ 2026-11-01 | Calendario público `GET /api/hotel/{hotSec}/unavailable-dates` (días sin unidades libres). 54/54 tests |
| 2 · Bloque B | ⏳ siguiente | Cruce de disponibilidad + lock anti-doble-reserva (activar `1011 UNIT_UNAVAILABLE`) |
| 3 | ⬜ | Unidad automática para tipos de una sola unidad |
| 4 | ⬜ | Búsqueda por disponibilidad |
| 5 | ⬜ | Detalle del hotel con unidades libres |
| 6 | ⬜ | Frontend (SweetNestUI) |
| 7 | ⬜ | Datos seed, pruebas y docs |

Pendientes sueltos: desasignar anfitrión (`hostId` vacío en update); documentar en `API.md`
los endpoints de la Fase 1.

---

## 1. Objetivo

Permitir que cualquier visitante busque alojamientos por destino y rango de fechas, y que un usuario con sesión pueda reservar.

Requerimiento de la búsqueda (resumen):

- Bloque con título visible y párrafo descriptivo.
- Calendario doble para elegir rango de fechas.
- Botón claro de "Realizar búsqueda".
- Resultados relevantes y precisos según lo ingresado.
- Autocompletado y sugerencias en el campo de búsqueda.
- Al mostrar resultados, se mantienen las secciones de categorías y recomendaciones del home.

---

## 2. Decisiones tomadas

| # | Decisión | Motivo |
|---|---|---|
| 1 | Lo único que se reserva es la **unidad** | Una sola lógica de disponibilidad para todos los tipos |
| 2 | Todo alojamiento tiene al menos una unidad | Casa y apartamento reciben una unidad automática |
| 3 | El tipo de alojamiento es un catálogo con marca de "varias unidades" | La marca decide si el formulario deja crear habitaciones |
| 4 | Reserva → unidad, nunca → hotel | Evita una regla de "uno u otro" en la base de datos |
| 5 | Un alojamiento aparece en la búsqueda si tiene al menos una unidad libre y con capacidad | Cumple "resultados relevantes y precisos" |
| 6 | El día de salida queda libre para otra reserva | Regla estándar de hospedaje |
| 7 | La búsqueda y el detalle son públicos; reservar exige sesión | El home debe funcionar sin iniciar sesión |

---

## 3. Modelo de datos

### Relaciones

| Relación | Cardinalidad | Clave foránea en |
|---|---|---|
| Tipo → Hotel | Uno a muchos | `hotels` |
| Hotel → Unidad | Uno a muchos | `hotel_units` |
| Unidad → Reserva | Uno a muchos | `reservations` |
| Usuario → Reserva | Uno a muchos | `reservations` |

No se necesita ninguna tabla intermedia. Un muchos a muchos solo aparecería si una reserva pudiera abarcar varias unidades en una sola operación, y queda fuera de este sprint.

### Tablas

**`hotel_types`** (nueva, catálogo)

| Columna | Tipo | Nota |
|---|---|---|
| id | entero, PK | |
| name | texto | Hotel, Casa, Apartamento |
| multi_unit | S/N | Si es S, el formulario permite crear varias unidades |
| state | A/I | |

**`hotels`** (existente, se ajusta)

| Cambio | Detalle |
|---|---|
| Agregar `type_id` | FK obligatoria hacia `hotel_types` |
| `hotCost` | Pendiente de decisión: se elimina o queda como "desde $X" calculado |

El resto de columnas, y las relaciones con ciudad, categorías, características e imágenes, no cambian.

**`hotel_units`** (nueva)

| Columna | Tipo | Nota |
|---|---|---|
| id | entero, PK | |
| hotel_id | FK obligatoria | Hacia `hotels` |
| name | texto | Ej: "Habitación 301". En casas, el nombre del alojamiento |
| capacity | entero | Huéspedes máximos |
| price_per_night | entero | Fuente de verdad del precio |
| state | A/I | |

**`reservations`** (nueva)

| Columna | Tipo | Nota |
|---|---|---|
| id | entero, PK | |
| user_id | FK obligatoria | Hacia `users` |
| unit_id | FK obligatoria | Hacia `hotel_units` |
| check_in | fecha | |
| check_out | fecha | Debe ser posterior a `check_in` |
| guests | entero | |
| price_per_night | entero | Copia congelada al reservar |
| total_price | entero | Calculado en el backend |
| status | texto | PENDING, CONFIRMED, CANCELLED, COMPLETED |
| created_at | fecha y hora | |

---

## 4. Reglas de negocio

### Disponibilidad

- Una unidad está libre si no tiene reservas activas que se crucen con el rango pedido.
- Estados que ocupan la unidad: PENDING y CONFIRMED.
- Estados que no la ocupan: CANCELLED y COMPLETED.
- Cruce de rangos: la reserva existente empieza antes de la salida pedida y termina después de la entrada pedida.
- Un hotel aparece en la búsqueda si tiene al menos una unidad activa, con capacidad suficiente y libre en el rango.

### Crear una reserva

1. El usuario sale del token, nunca del body de la petición.
2. `check_out` posterior a `check_in`.
3. `check_in` no puede estar en el pasado.
4. `guests` no puede superar la capacidad de la unidad.
5. La unidad y su hotel deben estar activos.
6. No debe haber una reserva activa que se cruce con las fechas.
7. `total_price` = noches × precio por noche, calculado en el backend.
8. Se guarda con estado PENDING y el precio congelado.

### Unidad automática

- Al crear un hotel cuyo tipo tiene `multi_unit` en N, el sistema crea una unidad con el nombre del hotel, su capacidad y su precio.
- Al crear un hotel cuyo tipo tiene `multi_unit` en S, queda inactivo hasta que cree su primera unidad.

---

## 5. Endpoints

| Método | Ruta | Acceso | Uso |
|---|---|---|---|
| GET | `/api/hotel-types` | Público | Maestra para el formulario y los filtros |
| GET | `/api/cities/suggestions?q=` | Público | Autocompletado del destino, máximo 8 resultados |
| GET | `/api/hotels/search` | Público | Destino, fechas, huéspedes, paginado |
| GET | `/api/hotels/{id}` | Público | Detalle del hotel (ya existe) |
| GET | `/api/hotels/{id}/units` | Público | Unidades libres para un rango de fechas |
| POST | `/api/reservations` | Con sesión | Crear reserva |
| GET | `/api/reservations/me` | Con sesión | Mis reservas |
| PATCH | `/api/reservations/{id}/cancel` | Con sesión | Cancelar |

Los GET públicos se habilitan en la configuración de seguridad, filtrando por método HTTP. Todas las respuestas usan el `ApiResponse` ya existente.

---

## 6. Plan por fases

### Fase 0: decisiones pendientes
- [ ] Nombres de columnas en tablas nuevas: limpios (`id`, `name`) o con prefijo como `hotels` (`hotSec`, `hotName`).
- [ ] `hotCost`: eliminarlo o dejarlo como "desde $X" calculado.
- [ ] Tipos de alojamiento: tabla (administrable) o enum (fijo).
- [ ] La reserva se confirma sola o la aprueba un administrador.
- [ ] Detalle sin fechas: mostrar todas las unidades o pedir las fechas antes.

### Fase 1: tipos de alojamiento
- [ ] Crear `hotel_types` con sus tres valores iniciales.
- [ ] Agregar `type_id` a `hotels`.
- [ ] Ajustar el DTO y el mapper del hotel para incluir el tipo.
- [ ] Endpoint de lectura de tipos.

### Fase 2: unidades
- [ ] Crear `hotel_units` y su repositorio.
- [ ] Regla de unidad automática para tipos de una sola unidad.
- [ ] Regla de hotel inactivo hasta tener su primera unidad.
- [ ] CRUD de unidades para hoteles con `multi_unit` en S.

### Fase 3: reservas
- [ ] Crear `reservations` y el enum de estados.
- [ ] Consulta de cruce de fechas.
- [ ] Servicio con las ocho reglas de la sección 4.
- [ ] Endpoint de crear reserva y excepciones propias con su manejo en `GlobalExceptionHandler`.

### Fase 4: búsqueda
- [ ] Consulta de hoteles con al menos una unidad libre.
- [ ] Endpoint de búsqueda paginado.
- [ ] Endpoint de sugerencias de ciudad.
- [ ] Habilitar rutas públicas en la seguridad.

### Fase 5: detalle del hotel
- [ ] Incluir el listado de unidades libres en el detalle cuando el tipo es multi-unidad.
- [ ] Precio "desde $X" en las tarjetas.

### Fase 6: frontend
- [ ] Bloque de búsqueda con título, descripción, campo de destino con autocompletado y calendario doble.
- [ ] Botón "Realizar búsqueda".
- [ ] Resultados sin quitar categorías ni recomendaciones del home.
- [ ] Selección de unidad en el detalle y pantalla de confirmar reserva.
- [ ] Redirección a login al reservar sin sesión, y regreso a la unidad elegida.

### Fase 7: datos y pruebas
- [ ] Actualizar `DataInitializer` con tipos, unidades por hotel y algunas reservas de ejemplo.
- [ ] Probar los casos de la sección 7.

---

## 7. Casos de prueba de disponibilidad

| Caso | Resultado esperado |
|---|---|
| Hotel con 3 unidades, una ocupada en el rango | Aparece |
| Hotel con todas sus unidades ocupadas en el rango | No aparece |
| Reserva existente termina el día que empieza la nueva | No hay cruce |
| Reserva cancelada en el mismo rango | No bloquea |
| Huéspedes mayores a la capacidad de todas las unidades | No aparece |
| Casa con su única unidad reservada en el rango | No aparece |
| Hotel sin unidades | No aparece |
| Búsqueda sin token | Funciona |
| Crear reserva sin token | Rechazada |
| Dos reservas simultáneas a la misma unidad | Solo una se guarda |

---

## 8. Riesgos

| Riesgo | Mitigación |
|---|---|
| Hoteles existentes sin unidades desaparecen de la búsqueda | Regenerarlos desde el `DataInitializer` con sus unidades |
| Doble reserva simultánea sobre la misma unidad | Revalidar al guardar. Bloqueo de la unidad como mejora posterior |
| `hotCost` desactualizado frente al precio de las unidades | Decidirlo en la Fase 0 |
| Token vencido enviado a un endpoint público | El filtro JWT debe continuar la cadena si el token es inválido |

---

## 9. Fuera de alcance

- Reservar varias unidades en una sola operación.
- Bloqueo manual de fechas por el anfitrión.
- Tarifas por temporada.
- Pagos.
- Reseñas y calificaciones.
