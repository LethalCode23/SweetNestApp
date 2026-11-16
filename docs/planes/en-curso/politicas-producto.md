# Plan — HU #26: Políticas del producto (backend)

Plan del frontend: `../SweetNestUI/SweetNestUI/docs/planes/en-curso/politicas-producto.md`.

## Estado

| Fase | Estado | Contenido |
|---|---|---|
| 1 | ✅ 2026-11-12 | Tabla `hotel_policies`, políticas en `findDetailById`, seed de 6 por hotel |
| 2 | ✅ 2026-11-12 | Edición con el hotel (`HotelDto.policies`, `replacePolicies`), `HotelPoliciesTests`. 65/65 |
| 3 | ⏳ | Commit + revisión de punta a punta con el frontend |

Detalle → `docs/BITACORA.md` (2026-11-12).

---

## Fase 1

- [x] `entity/HotelPolicy` → `hotel_policies` (`polSec`, `polTitle`, `polDescription`, `polOrder`, FK `polHotSec`)
- [x] `Hotel.policies` `@OneToMany` (`cascade=ALL`, `orphanRemoval`, `@OrderBy("polOrder ASC")`)
- [x] `HotelResponseDto.policies` en `GET /api/hotel/findDetailById/{id}` (público)
- [x] `DataInitializer.addPolicies`: 6 políticas estándar por hotel

## Fase 2

- [x] `HotelDto.policies` + `@Valid` en `save`/`update`; `HotelPolicyDto` `@NotBlank`/`@Size` (100 / 1000)
- [x] `HotelService.replacePolicies`: `null` = no cambia, lista = reemplaza en orden, `[]` = borra
- [x] `HotelMapper.toListDto` devuelve `policies`
- [x] `HotelPoliciesTests` (4 casos) · `mvnw test` 65/65

## Fase 3

- [ ] Commit (`feat: politicas del producto (HU #26)`)
- [ ] Crear/editar políticas desde el panel y verlas en el detalle
