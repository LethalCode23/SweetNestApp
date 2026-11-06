# Sugerencias de búsqueda (autocompletado)

## Estado
- ✅ Fase 1 — Backend: `GET /api/hotel/suggestions` (2026-10-31)
- ⬜ Frontend (repo `SweetNestUI`, fuera de este plan): autocompletado en `HotelFilters.jsx`

## Ticket
"Al ingresar datos en el campo de búsqueda, se deben ofrecer sugerencias y autocompletar las
palabras clave relevantes."

## Decisiones (sobre el plan genérico `/api/products/suggestions`)
1. Ruta `api/hotel/suggestions`, siguiendo la convención del proyecto: sin plural, dentro de `HotelController`.
2. Sugiere **ciudades + hoteles** con `type` (`CITY`/`HOTEL`), porque el buscador real
   (`findByHotel?citName=`) filtra por ciudad. Si solo sugiriera hoteles, elegir uno no
   devolvería resultados.
3. Público (`permitAll` GET en `SecurityConfiguration`).
4. `q` opcional; si falta o tiene menos de 2 caracteres tras `trim`, devuelve `200 []`. Nunca `400`: un parámetro
   requerido ausente caería en el handler genérico y daría `500`.
5. Solo activos (`hotState = 'A'`, `citState = 'A'`). Ciudades primero, luego hoteles; tope 10.
6. JPQL con `select new SearchSuggestionDto(...)` + `Pageable`: no carga entidades.
7. Respuesta como lista cruda (igual que el resto de `api/hotel`).

## Contrato
`GET /api/hotel/suggestions?q=bogo` →
```json
[
  { "type": "CITY",  "id": 1, "name": "Bogotá" },
  { "type": "HOTEL", "id": 4, "name": "Hostal Andino Bogotá" }
]
```
Front: `CITY` → setear `citName` y buscar · `HOTEL` → navegar a `findDetailById/{id}`.
Recomendado: debounce de ~300 ms y no llamar con menos de 2 caracteres.
