# SweetNest — Plan de Pruebas Backend

> Plan de pruebas manuales de la API REST de SweetNest, ejecutadas con **cURL** contra el entorno de desarrollo.
> Fecha de ejecución: **10/10/2026** · Entorno: `http://localhost:8080` · Base de datos: H2 en memoria (datos semilla).

## 1. Introducción

Este documento define y ejecuta las pruebas manuales del backend Spring Boot. Complementa:

- [API.md](./API.md) — documentación de los 53 endpoints con ejemplos cURL.
- [README.md](./README.md) — plan de pruebas de UI/integración del Sprint 1 (TC-01 a TC-04).

**Credenciales del entorno (datos semilla):**

| Email | Contraseña | Perfil |
| --- | --- | --- |
| `admin@sweetnest.com` | `Admin123` | Administrador |

## 2. Metodología

- Pruebas **manuales** ejecutadas vía cURL contra `localhost:8080`.
- Cada caso define: acción, resultado esperado y estado real observado.
- El token JWT se obtiene con `POST /api/auth/login` y se envía en `Authorization: Bearer <token>`.
- Se probaron flujos felices y casos de error (401, 403, 404, 400).

## 3. Plan de pruebas y ejecución

### 3.1 Autenticación (AuthController)

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-01 | Login con credenciales seed válidas | `200` + token JWT en `data.auth.token` | ✅ Pass |
| TC-BE-02 | Login con contraseña incorrecta | `401 Unauthorized` | ✅ Pass |
| TC-BE-03 | Registro de usuario nuevo | `200` con `userSec` asignado | ✅ Pass |
| TC-BE-04 | Registro con email ya existente | `400 Bad Request` | ❌ **Fail (anomalía)** |
| TC-BE-05 | Registro sin campos requeridos (`{}`) | `400 Bad Request` por validación | ✅ Pass |

> **Anomalía TC-BE-04:** registrar un email duplicado devuelve `500` (`code: 9000`, "Error interno del servidor") en lugar de `400`. El endpoint no maneja el caso de email en uso. *Recomendación:* capturar la duplicidad y responder `400` con mensaje claro.

### 3.2 Autorización / Seguridad

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-06 | Endpoint protegido **sin** token (`GET /api/category/all`) | `403 Forbidden` | ✅ Pass |
| **TC-BE-07** | Endpoint protegido **con** token válido | `200 OK` | ✅ Pass |

### 3.3 CRUD — Categorías (CategoryController)

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-08 | Crear categoría válida (`POST /save`) | `201 Created` con `catSec` | ✅ Pass |
| TC-BE-09 | Buscar categoría existente (`findById`) | `200 OK` | ✅ Pass |
| TC-BE-10 | Buscar categoría inexistente (id `9999`) | `404 Not Found` | ✅ Pass |
| TC-BE-11 | Actualizar categoría (`PUT /update/{id}`) | `200 OK` con datos nuevos | ✅ Pass |
| TC-BE-12 | Eliminar categoría creada (`DELETE /delete/{id}`) | `204 No Content` | ✅ Pass |
| TC-BE-13 | Crear categoría sin `catName` (body inválido) | `400 Bad Request` | ❌ **Fail (anomalía)** |

> **Anomalía TC-BE-13:** `CategoryDto` no tiene validación de campos obligatorios; el endpoint crea la categoría con `catName: null` y responde `201`. *Recomendación:* agregar `@NotBlank` en `catName` (validación `@Valid` en el controller).

### 3.4 Hoteles (HotelController)

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-14 | Listar hoteles paginado (`GET /all?page=0&size=5`) | `200 OK` con estructura `Page` | ✅ Pass |
| TC-BE-15 | Buscar con filtros sin token (`findByHotel`, público) | `200 OK` | ✅ Pass |
| TC-BE-16 | Detalle completo sin token (`findDetailById/1`, público) | `200 OK` con `city`, `imageUrls`, `categories` | ✅ Pass |
| TC-BE-17 | Detalle de hotel inexistente (id `9999`) | `404 Not Found` | ✅ Pass |
| TC-BE-18 | Crear hotel con categorías (`POST /save`) | `201 Created` | ✅ Pass |

### 3.5 Imágenes de hotel (HotelImagesController)

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-19 | Subir imagen **sin** token (multipart) | `403 Forbidden` | ✅ Pass |

> No se ejecutó el upload con token válido (requiere archivo de imagen real); queda como caso **pendiente** para la suite de imágenes.

### 3.6 RBAC — Perfiles y permisos

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-20 | Obtener módulos del perfil (`GET /api/profiles/1/modules`) | `200 OK` con lista de módulos | ✅ Pass |
| TC-BE-21 | Actualizar acceso a módulo (`PATCH .../entry-allowed`) | `200 OK` | ✅ Pass |
| TC-BE-22 | Otorgar acceso a submódulo (`POST /api/PermissionProMod/...`) | `200 OK` | ✅ Pass |
| TC-BE-23 | Revocar acceso a submódulo (`DELETE /api/PermissionProMod/...`) | `200 OK` | ✅ Pass |

### 3.7 Correos (EmailController)

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-24 | Enviar correo válido (`POST /send-simple-email`) | `200 OK` | ✅ Pass |
| TC-BE-25 | Enviar correo con body incompleto | `400 Bad Request` | ✅ Pass |

### 3.8 Usuarios (UserController)

| ID | Caso de prueba | Resultado esperado | Estado |
| --- | --- | --- | --- |
| TC-BE-26 | Listar usuarios (`GET /all`) | `200 OK` con lista | ✅ Pass |
| TC-BE-27 | Buscar usuario por email (`findByEmail/admin%40sweetnest.com`) | `200 OK` con perfil | ✅ Pass |

## 4. Resumen de resultados

| Métrica | Valor |
| --- | --- |
| Total de casos ejecutados | 27 |
| Pass ✅ | 25 |
| Fail ❌ | 2 (anomalías TC-BE-04 y TC-BE-13) |
| Cobertura de módulos | Auth, Autorización, CRUD, Hoteles, Imágenes, RBAC, Email, Users |
| Fecha de ejecución | 10/10/2026 |

### Hallazgos / Anomalías detectadas

1. **Registro con email duplicado → 500** (TC-BE-04): falta manejo de duplicidad; debería ser `400`.
2. **`CategoryDto` sin validación de obligatoriedad** (TC-BE-13): permite crear categorías con nombre nulo (`201`); debería ser `400`.

Ambas anomalías son mejoras de validación en el servidor, no bloquean los flujos principales.

### Casos pendientes

- Upload de imagen con token válido y archivo real (TC-BE-19 fue solo la variante sin token).
- Errores SMTP: no se forzó un fallo de envío (el SMTP del entorno funcionó en TC-BE-24).

## 5. Conclusión

El backend cubre correctamente los flujos principales: autenticación JWT, autorización (403 sin token), CRUD sobre maestras, búsqueda y detalle de hoteles (incluidos endpoints públicos), permisos RBAC y envío de correos. Se detectaron **2 anomalías de validación** (email duplicado y `catName` obligatorio) recomendadas para corregir en un siguiente sprint.
