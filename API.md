# SweetNest API — Documentación de Endpoints / Endpoint Documentation

> **SweetNest** — Backend para sistema de reservas de alojamiento / Hotel booking system backend.
> Spring Boot 4 · Java 17 · Spring Security (JWT) · Spring Data JPA · H2

## Índice / Index

- [Introducción / Introduction](#introducción--introduction)
- [Cómo probar la API / How to test](#cómo-probar-la-api--how-to-test)
- [Autenticación / Authentication](#autenticación--authentication)
- [Convenciones / Conventions](#convenciones--conventions)
- [1. AuthController — `/api/auth`](#1-authcontroller--apiauth)
- [2. CategoryController — `/api/category`](#2-categorycontroller--apicategory)
- [3. CityController — `/api/city`](#3-citycontroller--apicity)
- [4. DepartmentController — `/api/department`](#4-departmentcontroller--apidepartment)
- [5. FeatureController — `/api/feature`](#5-featurecontroller--apifeature)
- [6. HotelController — `/api/hotel`](#6-hotelcontroller--apihotel)
- [7. HotelImagesController — `/api/hotel-images`](#7-hotelimagescontroller--apihotel-images)
- [8. ModuleController — `/api/module`](#8-modulecontroller--apimodule)
- [9. PaisController — `/api/country`](#9-paiscontroller--apicountry)
- [10. PermissionProfilesModuleController — `/api/PermissionProMod`](#10-permissionprofilesmodulecontroller--apipermissionpromod)
- [11. PermissionProfilesModulePerController — `/api/PermissionProModPer`](#11-permissionprofilesmodulepercontroller--apipermissionpromodper)
- [12. ProfileController — `/api/profile`](#12-profilecontroller--apiprofile)
- [13. RbacController — `/api/profiles`](#13-rbaccontroller--apiprofiles)
- [14. UserController — `/api/users`](#14-usercontroller--apiusers)
- [15. EmailController — `/api/emails`](#15-emailcontroller--apiemails)
- [16. Datos de inicialización / Seed Data](#16-datos-de-inicialización--seed-data)
- [Resumen general / Summary](#resumen-general--summary)

---

## Introducción / Introduction

Base URL / Base URL: `http://localhost:8080`

Todos los endpoints comparten el prefijo `/api` (/ All endpoints share the `/api` prefix). La aplicación permite gestionar países, departamentos, ciudades, categorías, características, hoteles, imágenes de hoteles, usuarios, perfiles (roles) y permisos RBAC. It also provides JWT authentication.

---

## Cómo probar la API / How to test

### 1. Levantar el servidor / Start the server

Requisito / Requirement: **Java 17+**. No requiere base de datos externa (H2 en memoria). / No external database required (in-memory H2).

```bash
# Windows
./mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

| Recurso / Resource | URL |
| --- | --- |
| Base URL | `http://localhost:8080` |
| Swagger UI (interfaz interactiva) | `http://localhost:8080/swagger-ui.html` |
| Spec OpenAPI (JSON) | `http://localhost:8080/v3/api-docs` |
| Consola H2 / H2 console | `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:SweetNestDB`, usuario/user `sa`, contraseña/password `sa`) |

### 2. Obtener el token JWT / Get the JWT token

Los endpoints protegidos requieren un token. Inicia sesión con un usuario seed: / Protected endpoints require a token. Log in with a seed user:

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@sweetnest.com", "password": "Admin123"}'
```

Respuesta (extrae el valor de `data.auth.token`) / Response (extract the value of `data.auth.token`):

```json
{
  "success": true,
  "message": "Login exitoso.",
  "code": 0,
  "data": {
    "user": { "userSec": 1, "firstName": "Admin", "lastName": "SweetNest", "email": "admin@sweetnest.com" },
    "auth": {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer"
    }
  }
}
```

### 3. Usar el token en cada petición / Use the token on every request

Guarda el token en una variable y envíalo como header `Authorization: Bearer <token>`. / Save the token in a variable and send it as an `Authorization: Bearer <token>` header.

```bash
# Bash (Git Bash, macOS, Linux)
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
curl "http://localhost:8080/api/category/all" \
  -H "Authorization: Bearer $TOKEN"
```

```powershell
# PowerShell (Windows)
$env:TOKEN = "eyJhbGciOiJIUzI1NiJ9..."
curl.exe "http://localhost:8080/api/category/all" -H "Authorization: Bearer $env:TOKEN"
```

> Los endpoints marcados como **públicos** van **sin** el header de autorización. / Endpoints marked as **public** go **without** the authorization header.

Todos los ejemplos `curl` de este documento usan la variable `$TOKEN` (Bash). / All `curl` examples in this document use the `$TOKEN` variable (Bash).

---

## Autenticación / Authentication

El servicio usa **JWT (Bearer token)**. Para acceder a los recursos protegidos:

1. Regístrate en `POST /api/auth/register` o inicia sesión en `POST /api/auth/login`.
2. El `LoginResponseDto` devuelve un token JWT (`auth.token`).
3. Envía el token en cada request: `Authorization: Bearer <token>`.

### Endpoints públicos (sin autenticación) / Public endpoints (no auth required)

| Endpoint | Método |
| --- | --- |
| `/api/auth/**` | POST |
| `/api/hotel/findByHotel/**` | GET |
| `/api/hotel/findDetailById/**` | GET |
| `/hotel-images/**` | GET |
| `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**` | GET |
| `/h2-console/**` | — |

Todo lo demás exige autenticación / Everything else requires authentication.

---

## Convenciones / Conventions

### Envelope de respuesta `ApiResponse`

Los controladores con soporte i18n devuelven un objeto `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "mensaje / message",
  "code": 0,
  "data": { }
}
```

| Campo / Field | Tipo | Descripción |
| --- | --- | --- |
| `success` | `boolean` | Indica si la operación fue exitosa |
| `message` | `string` | Mensaje (español/inglés según i18n) |
| `code` | `int` | Código del resultado (`0` = éxito; ver `ApiResponseCode`) |
| `data` | `T` | Datos de la respuesta (ausente en errores) |

### Paginación / Pagination

Los endpoints con `Page<T>` aceptan dos query params:

| Parámetro | Valor por defecto | Descripción |
| --- | --- | --- |
| `page` | `0` | Índice de página (base 0) / Page index (0-based) |
| `size` | `10` | Tamaño de página / Page size |

Respuesta (estructura estándar de Spring `Page`):

```json
{
  "content": [ ],
  "pageable": { },
  "totalPages": 1,
  "totalElements": 3,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": { "empty": true, "sorted": false, "unsorted": true },
  "first": true,
  "numberOfElements": 3,
  "empty": false
}
```

### Códigos HTTP comunes / Common HTTP codes

| Código | Significado / Meaning |
| --- | --- |
| `200 OK` | Éxito con cuerpo / Success with body |
| `201 Created` | Recurso creado / Resource created |
| `204 No Content` | Éxito sin cuerpo / Success without body |
| `400 Bad Request` | Datos inválidos / Invalid data |
| `401 Unauthorized` | Credenciales incorrectas en login / Wrong login credentials |
| `403 Forbidden` | Sin token o token inválido en endpoint protegido / Missing or invalid token on a protected endpoint |
| `404 Not Found` | Recurso no encontrado |
| `500 Internal Server Error` | Error del servidor |

---

## 1. AuthController — `/api/auth`

Controlador de autenticación (registro y login con JWT). No requiere token previo.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/auth/register` | Registrar un nuevo usuario |
| POST | `/api/auth/login` | Iniciar sesión y obtener JWT |

---

### `POST /api/auth/register`

Registrar un nuevo usuario en el sistema.

- **Autenticación:** N/A (pública).
- **Body (JSON):**

```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@example.com",
  "password": "secreto123",
  "profileId": 1
}
```

| Campo | Tipo | Requerido | Descripción |
| --- | --- | --- | --- |
| `firstName` | `string` | Sí | Nombre / First name |
| `lastName` | `string` | Sí | Apellido / Last name |
| `email` | `string` | Sí | Email válido / Valid email |
| `password` | `string` | Sí (mín 6) | Contraseña / Password |
| `profileId` | `Long` | No | ID del perfil/rol a asignar |

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "password": "secreto123",
    "profileId": 1
  }'
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Usuario registrado exitosamente.",
  "code": 0,
  "data": {
    "user": {
      "userSec": 1,
      "firstName": "Juan",
      "lastName": "Pérez",
      "email": "juan.perez@example.com",
      "profile": {
        "proId": 1,
        "proName": "Administrador",
        "proState": "A",
        "isDefault": "N",
        "hasControlAccess": "S"
      }
    }
  }
}
```

- **Errores:** `400` si la validación falla o el email ya está en uso.

---

### `POST /api/auth/login`

Iniciar sesión y obtener el token JWT.

- **Autenticación:** N/A (pública).
- **Body (JSON):**

```json
{
  "email": "juan.perez@example.com",
  "password": "secreto123"
}
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@sweetnest.com", "password": "Admin123"}'
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Login exitoso.",
  "code": 0,
  "data": {
    "user": {
      "userSec": 1,
      "firstName": "Juan",
      "lastName": "Pérez",
      "email": "juan.perez@example.com",
      "profile": { "proId": 1, "proName": "Administrador", "proState": "A", "isDefault": "N", "hasControlAccess": "S" }
    },
    "auth": {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "tokenType": "Bearer"
    }
  }
}
```

- **Errores:** `401` si las credenciales son incorrectas.

---

## 2. CategoryController — `/api/category`

Gestión de categorías de hoteles (CRUD completo). Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/category/save` | Crear categoría |
| GET | `/api/category/findById/{id}` | Buscar por ID |
| GET | `/api/category/all` | Listar todas |
| PUT | `/api/category/update/{id}` | Actualizar |
| DELETE | `/api/category/delete/{id}` | Eliminar |

**DTO / Body fields (`CategoryDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `catSec` | `Integer` | ID (autogenerado; no enviar al crear) |
| `catName` | `String` | Nombre de la categoría |
| `catEst` | `Character` | Estado (`A` activo, `I` inactivo) |

---

### `POST /api/category/save`

Crea una categoría. / Creates a category.

- **Body:**

```json
{ "catName": "Hotel 5 estrellas", "catEst": "A" }
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/category/save" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "catName": "Hotel 5 estrellas", "catEst": "A" }'
```

- **Respuesta `201 Created`:** devuelve el `CategoryDto` creado (con `catSec` asignado) / returns the created DTO with its generated ID.
- **Errores:** `400` si el body es inválido.

### `GET /api/category/findById/{id}`

Busca una categoría por su ID. / Finds a category by ID.

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl "http://localhost:8080/api/category/findById/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `CategoryDto` correspondiente. / matching category.
- **Errores:** `404` si no existe.

### `GET /api/category/all`

Lista todas las categorías. / Lists all categories.

- **cURL:**

```bash
curl "http://localhost:8080/api/category/all" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `[CategoryDto, ...]`
- **Errores:** `403` sin token válido.

### `PUT /api/category/update/{id}`

Actualiza la categoría indicada por `id`. / Updates the category identified by `id`.

- **Path var:** `id` (`Integer`)
- **Body:** `CategoryDto` con los datos nuevos (incluye `catName`, `catEst`; puede incluir `catSec`).
- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/category/update/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "catSec": 1, "catName": "Hotel Boutique", "catEst": "A" }'
```

- **Respuesta `200 OK`:** `CategoryDto` actualizado.
- **Errores:** `404` si no existe; `400` si el body es inválido.

### `DELETE /api/category/delete/{id}`

Elimina la categoría indicada por `id`. / Deletes the category identified by `id`.

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/category/delete/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `204 No Content`.**
- **Errores:** `404` si no existe.

---

## 3. CityController — `/api/city`

Gestión de ciudades (CRUD completo). / City management (full CRUD). Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/city/save` | Crear ciudad |
| GET | `/api/city/findById/{id}` | Buscar por ID |
| GET | `/api/city/all` | Listar todas |
| PUT | `/api/city/update/{id}` | Actualizar |
| DELETE | `/api/city/delete/{id}` | Eliminar |

**DTO (`CityDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `citSec` | `Integer` | ID (autogenerado) |
| `citName` | `String` | Nombre de la ciudad |
| `citDepSec` | `Integer` | ID del departamento al que pertenece |
| `citState` | `Character` | Estado (`A`/`I`) |

---

### `POST /api/city/save`

Crea una ciudad. / Creates a city.

- **Body:**

```json
{ "citName": "Bogotá", "citDepSec": 1, "citState": "A" }
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/city/save" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "citName": "Bogotá", "citDepSec": 1, "citState": "A" }'
```

- **Respuesta `201 Created`:** `CityDto` creado.

### `GET /api/city/findById/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl "http://localhost:8080/api/city/findById/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200`** `CityDto` / **`404`** si no existe.

### `GET /api/city/all`

- **cURL:**

```bash
curl "http://localhost:8080/api/city/all" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `[CityDto, ...]`

### `PUT /api/city/update/{id}`

- **Path var:** `id` (`Integer`) — **Body:** `CityDto`
- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/city/update/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "citSec": 1, "citName": "Bogotá D.C.", "citDepSec": 1, "citState": "A" }'
```

- **Respuesta `200`** `CityDto` actualizado.

### `DELETE /api/city/delete/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/city/delete/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `204 No Content`.**

---

## 4. DepartmentController — `/api/department`

Gestión de departamentos/estados (CRUD completo). / Department (state/province) management. Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/department/save` | Crear departamento |
| GET | `/api/department/all` | Listar todos |
| GET | `/api/department/findById/{id}` | Buscar por ID |
| PUT | `/api/department/update/{id}` | Actualizar |
| DELETE | `/api/department/delete/{id}` | Eliminar |

**DTO (`DepartmentDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `depSec` | `Integer` | ID (autogenerado) |
| `depName` | `String` | Nombre del departamento |
| `countryResponseDto` | `CountryResponseDto` | País (contiene `paiSec`, `paiName`) |
| `depState` | `Character` | Estado (`A`/`I`) |

---

### `POST /api/department/save`

Crea un departamento. / Creates a department.

- **Body:**

```json
{ "depName": "Cundinamarca", "countryResponseDto": { "paiSec": 1, "paiName": "Colombia" }, "depState": "A" }
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/department/save" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "depName": "Cundinamarca",
    "countryResponseDto": { "paiSec": 1, "paiName": "Colombia" },
    "depState": "A"
  }'
```

- **Respuesta `200 OK`:** `DepartmentDto` creado. (En `save` el servicio responde `200`, no `201`.)

### `GET /api/department/all`

- **cURL:**

```bash
curl "http://localhost:8080/api/department/all" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `[DepartmentDto, ...]`

### `GET /api/department/findById/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl "http://localhost:8080/api/department/findById/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200`** `DepartmentDto` / **`404`** si no existe.

### `PUT /api/department/update/{id}`

- **Path var:** `id` (`Integer`) — **Body:** `DepartmentDto`
- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/department/update/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "depSec": 1,
    "depName": "Cundinamarca",
    "countryResponseDto": { "paiSec": 1, "paiName": "Colombia" },
    "depState": "A"
  }'
```

- **Respuesta `200`** `DepartmentDto` actualizado.

### `DELETE /api/department/delete/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/department/delete/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `204 No Content`.**

---

## 5. FeatureController — `/api/feature`

Gestión de características/amenidades de hoteles. Usa el envelope `ApiResponse` con mensajes i18n. Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/feature` | Crear característica |
| GET | `/api/feature/all` | Listar todas |
| GET | `/api/feature/{id}` | Buscar por ID |
| PUT | `/api/feature/{id}` | Actualizar |
| DELETE | `/api/feature/{id}` | Eliminar |

**DTO (`FeatureDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `feaSec` | `Integer` | ID (autogenerado) |
| `feaName` | `String` | Nombre de la característica |
| `feaEst` | `Character` | Estado (`A`/`I`) |

---

### `POST /api/feature`

Crea una característica. / Creates a feature.

- **Body:**

```json
{ "feaName": "Piscina", "feaEst": "A" }
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/feature" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "feaName": "Piscina", "feaEst": "A" }'
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Característica creada exitosamente.",
  "code": 0,
  "data": { "feaSec": 1, "feaName": "Piscina", "feaEst": "A" }
}
```

- **Errores:** `400` con mensaje de validación si falta `feaName`.

### `GET /api/feature/all`

- **cURL:**

```bash
curl "http://localhost:8080/api/feature/all" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `ApiResponse<List<FeatureDto>>`.

### `GET /api/feature/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl "http://localhost:8080/api/feature/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200`** `ApiResponse<FeatureDto>` / **`404`** si no existe.

### `PUT /api/feature/{id}`

- **Path var:** `id` (`Integer`) — **Body:** `FeatureDto`
- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/feature/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "feaName": "Piscina climatizada", "feaEst": "A" }'
```

- **Respuesta `200`** `ApiResponse<FeatureDto>` actualizado.

### `DELETE /api/feature/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/feature/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`** con `ApiResponse<Void>` (`data: null`), **no** devuelve `204`.

---

## 6. HotelController — `/api/hotel`

Gestión de hoteles: CRUD, listado paginado y búsqueda por filtros. / Hotel management: CRUD, paginated listing and filter search.

> Nota / Note: `GET /api/hotel/findByHotel/**` es **público** (permite búsqueda sin token, según `SecurityConfiguration`). El resto requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/hotel/save` | Crear hotel |
| PUT | `/api/hotel/update/{id}` | Actualizar |
| GET | `/api/hotel/all?page=&size=` | Listar paginado |
| DELETE | `/api/hotel/delete/{id}` | Eliminar |
| GET | `/api/hotel/findById/{id}` | Buscar por ID |
| GET | `/api/hotel/findByHotel?page=&size=&citName=&minCost=&maxCost=&categoryIds=` | Buscar con filtros (público) |
| GET | `/api/hotel/findDetailById/{id}` | Detalle completo (público) |

**DTO (`HotelDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `hotSec` | `Integer` | ID (autogenerado) |
| `hotName` | `String` | Nombre del hotel |
| `hotDescription` | `String` | Descripción |
| `hotAddress` | `String` | Dirección |
| `hotCost` | `int` | Costo por noche |
| `hotState` | `Character` | Estado (`A`/`I`) |
| `hotCitSec` | `Integer` | ID de la ciudad |
| `hotCitName` | `String` | Nombre de la ciudad (solo lectura/respuesta) |
| `hotelImagesUrl` | `List<HotelImagesDto>` | Imágenes (respuesta) |
| `categoryIds` | `List<Integer>` | IDs de categorías (al crear/actualizar) |
| `categories` | `List<CategoryDto>` | Categorías (respuesta) |

---

### `POST /api/hotel/save`

Crea un hotel. / Creates a hotel.

- **Body:**

```json
{
  "hotName": "Hotel Sweet Dreams",
  "hotDescription": "Hotel boutique con vista al lago",
  "hotAddress": "Av. Principal 123",
  "hotCost": 250000,
  "hotState": "A",
  "hotCitSec": 1,
  "categoryIds": [1, 2]
}
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/hotel/save" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "hotName": "Hotel Sweet Dreams",
    "hotDescription": "Hotel boutique con vista al lago",
    "hotAddress": "Av. Principal 123",
    "hotCost": 250000,
    "hotState": "A",
    "hotCitSec": 1,
    "categoryIds": [1, 2]
  }'
```

- **Respuesta `201 Created`:** `HotelDto` creado (incluye `hotSec`, `hotCitName` y `categories`).

### `PUT /api/hotel/update/{id}`

- **Path var:** `id` (`Integer`) — **Body:** `HotelDto`
- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/hotel/update/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "hotSec": 1,
    "hotName": "Hotel Sweet Dreams",
    "hotDescription": "Hotel boutique con vista al lago",
    "hotAddress": "Av. Principal 123",
    "hotCost": 275000,
    "hotState": "A",
    "hotCitSec": 1,
    "categoryIds": [1, 2]
  }'
```

- **Respuesta `200`** `HotelDto` actualizado.

### `GET /api/hotel/all?page=0&size=10`

Lista paginada de hoteles. / Paginated list of hotels.

- **Query params:** `page` (default `0`), `size` (default `10`).
- **cURL:**

```bash
curl "http://localhost:8080/api/hotel/all?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`: `Page<HotelDto>`** (ver estructura de paginación arriba).

### `DELETE /api/hotel/delete/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/hotel/delete/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `204 No Content`.**

### `GET /api/hotel/findById/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl "http://localhost:8080/api/hotel/findById/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200`** `HotelDto` / **`404`** si no existe.

### `GET /api/hotel/findByHotel` — *(público / public)*

Es el endpoint principal de búsqueda de hoteles (el que consume el frontend en el home). Combina filtros opcionales mediante JPA Specifications.

- **Query params:**

| Parámetro | Tipo | Requerido | Descripción |
| --- | --- | --- | --- |
| `page` | `int` | No | Página (default `0`) |
| `size` | `int` | No | Tamaño (default `10`) |
| `citName` | `String` | No | Filtra por nombre de ciudad |
| `minCost` | `Integer` | No | Costo mínimo |
| `maxCost` | `Integer` | No | Costo máximo |
| `categoryIds` | `List<Integer>` | No | IDs de categorías (se puede repetir o pasar como `categoryIds=1&categoryIds=2`) |

- **Ejemplo:**

```
GET /api/hotel/findByHotel?page=0&size=10&citName=Bogotá&minCost=100000&maxCost=500000&categoryIds=1&categoryIds=2
```

- **cURL (sin token, es público):**

```bash
curl "http://localhost:8080/api/hotel/findByHotel?page=0&size=10&citName=Bogot%C3%A1&minCost=100000&maxCost=500000&categoryIds=1&categoryIds=2"
```

- **Respuesta `200 OK`: `Page<HotelDto>`** con los hoteles que cumplen los filtros.

### `GET /api/hotel/findDetailById/{id}` — *(público / public)*

Devuelve el detalle completo de un hotel: información básica, ciudad, imágenes, categorías y características. El frontend lo usa en la página de detalle del alojamiento. / Returns the full hotel detail: basic info, city, images, categories and features. Used by the frontend on the lodging detail page.

- **Path var:** `id` (`Integer`)
- **cURL (sin token, es público):**

```bash
curl "http://localhost:8080/api/hotel/findDetailById/1"
```

- **Respuesta `200 OK`: `ApiResponse<HotelResponseDto>`**

```json
{
  "success": true,
  "message": "Detalle del hotel encontrado.",
  "code": 0,
  "data": {
    "id": 1,
    "name": "Hotel Sweet Dreams",
    "description": "Hotel boutique con vista al lago",
    "address": "Av. Principal 123",
    "cost": 250000,
    "state": "A",
    "city": { "citSec": 1, "citName": "Bogotá", "citDepSec": 1, "citState": "A" },
    "imageUrls": ["/hotel-images/fachada.jpg"],
    "categories": [ { "catSec": 1, "catName": "Hotel 5 estrellas", "catEst": "A" } ],
    "features": [ { "feaSec": 1, "feaName": "Piscina", "feaEst": "A" } ]
  }
}
```

- **Errores:** `404` si no existe.

---

## 7. HotelImagesController — `/api/hotel-images`

Gestión de imágenes de hoteles (subida multipart y eliminación). / Hotel image management (multipart upload and deletion). Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/hotel-images/save` | Subir imagen (`multipart/form-data`) |
| DELETE | `/api/hotel-images/delete/{hotImgSec}` | Eliminar imagen por ID |

---

### `POST /api/hotel-images/save`

Sube una imagen y la asocia a un hotel. / Uploads an image and associates it with a hotel.

- **Content-Type:** `multipart/form-data`
- **Form fields:**

| Campo | Tipo | Obligatorio | Descripción |
| --- | --- | --- | --- |
| `hotSec` | `Integer` | Sí | ID del hotel al que pertenece |
| `hotImgPri` | `int` | Sí | Prioridad de la imagen (`1` = principal) |
| `file` | `MultipartFile` | Sí | Archivo de imagen |

- **Ejemplo (cURL):**

```bash
curl -X POST "http://localhost:8080/api/hotel-images/save" \
  -H "Authorization: Bearer $TOKEN" \
  -F "hotSec=1" \
  -F "hotImgPri=1" \
  -F "file=@foto.jpg"
```

- **Respuesta `200 OK`:**

```json
{
  "hotImgSec": 1,
  "hotSec": 1,
  "hotImgUrl": "/uploads/foto.jpg",
  "hotImgPri": 1
}
```

- **Errores:** `400` si el archivo es inválido (`InvalidFileException`); `500` si falla el almacenamiento (`FileStorageException`).

### `DELETE /api/hotel-images/delete/{hotImgSec}`

- **Path var:** `hotImgSec` (`Integer`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/hotel-images/delete/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `204 No Content`** si se eliminó correctamente; **`500`** en caso de error.

---

## 8. ModuleController — `/api/module`

Registro de módulos del sistema (RBAC). / System module registration (RBAC). Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/module/save` | Crear módulo |

---

### `POST /api/module/save`

Crea un módulo. / Creates a module.

- **Body (`ModuleDto`):**

```json
{
  "moduleSec": 0,
  "moduleName": "Hoteles",
  "moduleUrl": "/hoteles",
  "moduleDescription": "Gestión de hoteles",
  "moduleState": "A",
  "entryAllowed": true,
  "subModules": []
}
```

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `moduleSec` | `long` | ID (autogenerado si es `0`) |
| `moduleName` | `String` | Nombre del módulo |
| `moduleUrl` | `String` | Ruta del frontend |
| `moduleDescription` | `String` | Descripción |
| `moduleState` | `Character` | Estado (`A`/`I`) |
| `entryAllowed` | `Boolean` | Permite el acceso al módulo |
| `subModules` | `List<SubModuleDto>` | Submódulos anidados |

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/module/save" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "moduleSec": 0,
    "moduleName": "Hoteles",
    "moduleUrl": "/hoteles",
    "moduleDescription": "Gestión de hoteles",
    "moduleState": "A",
    "entryAllowed": true,
    "subModules": []
  }'
```

- **Respuesta `200 OK`:** `ModuleDto` creado.

---

## 9. PaisController — `/api/country`

Gestión de países (CRUD completo). / Country management (full CRUD). Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/country/save` | Crear país |
| GET | `/api/country/all` | Listar todos |
| GET | `/api/country/findById/{id}` | Buscar por ID |
| PUT | `/api/country/update/{id}` | Actualizar |
| DELETE | `/api/country/delete/{id}` | Eliminar |

**DTO (`PaisDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `paiSec` | `Integer` | ID (autogenerado) |
| `paiName` | `String` | Nombre del país |
| `paiState` | `Character` | Estado (`A`/`I`) |

---

### `POST /api/country/save`

Crea un país. / Creates a country.

- **Body:**

```json
{ "paiName": "Colombia", "paiState": "A" }
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/country/save" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "paiName": "Colombia", "paiState": "A" }'
```

- **Respuesta `200 OK`:** `PaisDto` creado.

### `GET /api/country/all`

- **cURL:**

```bash
curl "http://localhost:8080/api/country/all" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `[PaisDto, ...]`

### `GET /api/country/findById/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl "http://localhost:8080/api/country/findById/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `PaisDto` / **`404`** si no existe.

### `PUT /api/country/update/{id}`

- **Path var:** `id` (`Integer`) — **Body:** `PaisDto`
- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/country/update/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "paiSec": 1, "paiName": "Colombia", "paiState": "A" }'
```

- **Respuesta `200 OK`:** `PaisDto` actualizado.

### `DELETE /api/country/delete/{id}`

- **Path var:** `id` (`Integer`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/country/delete/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `204 No Content`.**

---

## 10. PermissionProfilesModuleController — `/api/PermissionProMod`

Otorga/revoca acceso a submódulos para un perfil (RBAC). / Grants/revokes submodule access for a profile. Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/PermissionProMod/{profileId}/modules/{moduleId}/submodules/{subModuleId}` | Otorgar acceso a submódulo |
| DELETE | `/api/PermissionProMod/{profileId}/modules/{moduleId}/submodules/{subModuleId}` | Revocar acceso a submódulo |

---

### `POST /api/PermissionProMod/{profileId}/modules/{moduleId}/submodules/{subModuleId}`

Otorga al perfil el acceso al submódulo indicado. / Grants submodule access to a profile.

- **Path vars:** `profileId` (`Long`), `moduleId` (`Long`), `subModuleId` (`Long`).
- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/PermissionProMod/1/modules/1/submodules/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Acceso al submódulo otorgado.",
  "code": 0,
  "data": null
}
```

### `DELETE /api/PermissionProMod/{profileId}/modules/{moduleId}/submodules/{subModuleId}`

Revoca el acceso al submódulo. / Revokes submodule access.

- **Path vars:** `profileId`, `moduleId`, `subModuleId` (`Long`).
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/PermissionProMod/1/modules/1/submodules/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Acceso al submódulo revocado.",
  "code": 0,
  "data": null
}
```

---

## 11. PermissionProfilesModulePerController — `/api/PermissionProModPer`

Configura los permisos por acción dentro de un submódulo (RBAC). / Configures per-action permissions inside a submodule. Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| PATCH | `/api/PermissionProModPer/{profileId}/modules/{moduleId}/submodules/{subModuleId}/actions/{code}` | Habilitar/deshabilitar una acción |

---

### `PATCH /api/PermissionProModPer/{profileId}/modules/{moduleId}/submodules/{subModuleId}/actions/{code}`

Cambia si una acción concreta está permitida para un perfil. / Toggles whether a specific action is allowed for a profile.

- **Path vars:** `profileId` (`Long`), `moduleId` (`Long`), `subModuleId` (`Long`), `code` (`Character`, código de la acción, ej. `'R'`, `'C'`, `'U'`, `'D'`).
- **Body (`UpdateActionAllowedRequest`):**

```json
{ "allowed": true }
```

| Campo | Tipo | Requerido | Descripción |
| --- | --- | --- | --- |
| `allowed` | `Boolean` | Sí | `true` permite la acción, `false` la bloquea |

- **cURL:**

```bash
curl -X PATCH "http://localhost:8080/api/PermissionProModPer/1/modules/1/submodules/1/actions/R" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "allowed": true }'
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Permiso de acción actualizado.",
  "code": 0,
  "data": null
}
```

---

## 12. ProfileController — `/api/profile`

Gestión de perfiles/roles (CRUD completo). / Profile (role) management. Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/profile/save` | Crear perfil |
| PUT | `/api/profile/{id}` | Actualizar |
| GET | `/api/profile/{id}` | Buscar por ID |
| GET | `/api/profile/all` | Listar todos |
| DELETE | `/api/profile/{id}` | Eliminar |

**DTO (`ProfileDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `proId` | `long` | ID (autogenerado) |
| `proName` | `String` | Nombre del perfil |
| `proState` | `Character` | Estado (`A`/`I`) |
| `isDefault` | `Character` | Si es perfil por defecto (`S`/`N`) |
| `hasControlAccess` | `Character` | Si tiene control de acceso RBAC (`S`/`N`) |

---

### `POST /api/profile/save`

Crea un perfil. / Creates a profile.

- **Body:**

```json
{ "proName": "Recepcionista", "proState": "A", "isDefault": "N", "hasControlAccess": "S" }
```

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/profile/save" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "proName": "Recepcionista", "proState": "A", "isDefault": "N", "hasControlAccess": "S" }'
```

- **Respuesta `201 Created`:** `ProfileDto` creado.

### `PUT /api/profile/{id}`

- **Path var:** `id` (`Long`) — **Body:** `ProfileDto`
- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/profile/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "proId": 1, "proName": "Administrador", "proState": "A", "isDefault": "S", "hasControlAccess": "S" }'
```

- **Respuesta `200`** `ProfileDto` actualizado.

### `GET /api/profile/{id}`

- **Path var:** `id` (`Long`)
- **cURL:**

```bash
curl "http://localhost:8080/api/profile/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200`** `ProfileDto` / **`404`** si no existe.

### `GET /api/profile/all`

- **cURL:**

```bash
curl "http://localhost:8080/api/profile/all" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `[ProfileDto, ...]`

### `DELETE /api/profile/{id}`

- **Path var:** `id` (`Long`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/profile/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `204 No Content`.**

---

## 13. RbacController — `/api/profiles`

Consulta de permisos por perfil y actualización del flag de acceso a módulo. / Profile permission queries and module entry-allowed update. Requiere autenticación.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| GET | `/api/profiles/{profileId}/modules` | Obtener módulos con acciones para un perfil |
| PATCH | `/api/profiles/{profileId}/modules/{moduleId}/entry-allowed` | Actualizar si el perfil puede entrar al módulo |

---

### `GET /api/profiles/{profileId}/modules`

Devuelve los módulos (con submódulos y acciones) configurados para un perfil. El frontend lo usa para renderizar el menú según permisos. / Returns the modules (with submodules and actions) configured for a profile; used by the frontend to render the menu by permissions.

- **Path var:** `profileId` (`Long`).
- **cURL:**

```bash
curl "http://localhost:8080/api/profiles/1/modules" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`: `List<ModuleDto>`**

```json
[
  {
    "moduleSec": 1,
    "moduleName": "Hoteles",
    "moduleUrl": "/hoteles",
    "moduleDescription": "Gestión de hoteles",
    "moduleState": "A",
    "entryAllowed": true,
    "subModules": [
      {
        "id": 1,
        "name": "Listado",
        "url": "/hoteles/listado",
        "actions": [
          { "code": "R", "name": "Ver", "description": "Consultar", "allowed": true },
          { "code": "C", "name": "Crear", "description": "Alta", "allowed": true }
        ]
      }
    ]
  }
]
```

### `PATCH /api/profiles/{profileId}/modules/{moduleId}/entry-allowed`

Actualiza si el perfil tiene permitido el acceso al módulo. / Updates whether the profile may access the module.

- **Path vars:** `profileId` (`Long`), `moduleId` (`Long`).
- **Body (`UpdateEntryAllowedRequest`):**

```json
{ "entryAllowed": true }
```

- **cURL:**

```bash
curl -X PATCH "http://localhost:8080/api/profiles/1/modules/1/entry-allowed" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "entryAllowed": true }'
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Permiso de entrada actualizado.",
  "code": 0,
  "data": null
}
```

---

## 14. UserController — `/api/users`

Gestión de usuarios. Usa el envelope `ApiResponse` con mensajes i18n. / User management. Requires authentication.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| GET | `/api/users/all` | Listar todos |
| GET | `/api/users/{userSec}` | Buscar por ID |
| GET | `/api/users/findByEmail/{email}` | Buscar por email |
| PUT | `/api/users/{userSec}` | Actualizar |
| DELETE | `/api/users/{userSec}` | Eliminar |

**DTO de salida (`UserDto`):**

| Campo | Tipo | Descripción |
| --- | --- | --- |
| `userSec` | `Long` | ID |
| `firstName` | `String` | Nombre |
| `lastName` | `String` | Apellido |
| `email` | `String` | Email |
| `profile` | `ProfileDto` | Perfil asociado |

---

### `GET /api/users/all`

Lista todos los usuarios. / Lists all users.

- **cURL:**

```bash
curl "http://localhost:8080/api/users/all" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Usuarios encontrados.",
  "code": 0,
  "data": [
    {
      "userSec": 1,
      "firstName": "Juan",
      "lastName": "Pérez",
      "email": "juan.perez@example.com",
      "profile": { "proId": 1, "proName": "Administrador", "proState": "A", "isDefault": "N", "hasControlAccess": "S" }
    }
  ]
}
```

### `GET /api/users/{userSec}`

- **Path var:** `userSec` (`Long`)
- **cURL:**

```bash
curl "http://localhost:8080/api/users/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200`** `ApiResponse<UserDto>` / **`404`** si no existe.

### `GET /api/users/findByEmail/{email}`

Busca por email (path param, no cuerpo). / Looks up by email (path param, not body).

- **Path var:** `email` (`String`). Escapa `@` como `%40` en la URL. / Escape `@` as `%40` in the URL.
- **cURL:**

```bash
curl "http://localhost:8080/api/users/findByEmail/admin%40sweetnest.com" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`:** `ApiResponse<UserDto>` / **`404`** si no existe.

### `PUT /api/users/{userSec}`

Actualiza los datos del usuario. / Updates user data.

- **Path var:** `userSec` (`Long`).
- **Body (`UpdateUserRequest`):**

```json
{
  "firstName": "Juan Carlos",
  "lastName": "Pérez",
  "email": "juan.perez@example.com",
  "profileId": 2
}
```

| Campo | Tipo | Requerido | Descripción |
| --- | --- | --- | --- |
| `firstName` | `String` | Sí (`@NotBlank`) | Nombre |
| `lastName` | `String` | Sí (`@NotBlank`) | Apellido |
| `email` | `String` | Sí (`@NotBlank`, `@Email`) | Email |
| `profileId` | `Long` | No | Nuevo perfil/rol |

- **cURL:**

```bash
curl -X PUT "http://localhost:8080/api/users/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan Carlos",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "profileId": 2
  }'
```

- **Respuesta `200 OK`:** `ApiResponse<UserDto>` actualizado.

### `DELETE /api/users/{userSec}`

- **Path var:** `userSec` (`Long`)
- **cURL:**

```bash
curl -X DELETE "http://localhost:8080/api/users/1" \
  -H "Authorization: Bearer $TOKEN"
```

- **Respuesta `200 OK`** `ApiResponse<Void>` (`data: null`).

---

## 15. EmailController — `/api/emails`

Envío de correos electrónicos (HTML vía Gmail SMTP). / Email sending (HTML via Gmail SMTP). Requiere autenticación.

> Nota: el envío depende del SMTP configurado en `application.properties`. Si las credenciales de Gmail no son válidas, el endpoint responde `500`. / The send depends on the SMTP configured in `application.properties`. If the Gmail credentials are not valid, the endpoint responds `500`.

### Resumen / Summary

| Método | Ruta | Propósito |
| --- | --- | --- |
| POST | `/api/emails/send-simple-email` | Enviar correo HTML |

---

### `POST /api/emails/send-simple-email`

Envía un correo HTML al destinatario indicado. / Sends an HTML email to the given recipient.

- **Autenticación:** requerida (JWT).
- **Body (`EmailRequestDTO`):**

```json
{
  "to": "destinatario@example.com",
  "subject": "Bienvenido a SweetNest",
  "body": "<h1>¡Hola!</h1><p>Gracias por usar SweetNest.</p>"
}
```

| Campo | Tipo | Requerido | Descripción |
| --- | --- | --- | --- |
| `to` | `String` | Sí (`@NotBlank`, `@Email`) | Destinatario / Recipient |
| `subject` | `String` | Sí (`@NotBlank`) | Asunto / Subject |
| `body` | `String` | Sí (`@NotBlank`) | Cuerpo HTML del mensaje / HTML body |

- **cURL:**

```bash
curl -X POST "http://localhost:8080/api/emails/send-simple-email" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "destinatario@example.com",
    "subject": "Bienvenido a SweetNest",
    "body": "<h1>¡Hola!</h1><p>Gracias por usar SweetNest.</p>"
  }'
```

- **Respuesta `200 OK`:**

```json
{
  "success": true,
  "message": "Correo enviado",
  "code": 0,
  "data": null
}
```

- **Errores:** `400` si la validación falla (campos vacíos o email inválido); `500` si falla el envío SMTP.

---

## 16. Datos de inicialización / Seed Data

La clase `DataInitializer` carga las maestras al arrancar, solo si la base de datos está vacía (`paisRepository.count() == 0`). / The `DataInitializer` class loads master data on startup, only when the database is empty.

| Maestra / Master data | Registros cargados / Records loaded |
| --- | --- |
| Perfiles / Profiles | 2 |
| Usuarios / Users | 2 |
| Módulos / Modules | 5 |
| Submódulos / SubModules | 8 |
| Países / Countries | 3 |
| Departamentos / Departments | 9 |
| Ciudades / Cities | 17 |
| Categorías / Categories | 3 |
| Hoteles / Hotels | 10 |

Usuarios por defecto / Default users:

| Email | Contraseña / Password | Perfil / Profile |
| --- | --- | --- |
| `admin@sweetnest.com` | `Admin123` | Administrador |
| `usuario@sweetnest.com` | `Usuario123` | Usuario |

---

## Resumen general / Summary

| Controlador / Controller | Base | Métodos |
| --- | --- | --- |
| AuthController | `/api/auth` | POST `/register`, POST `/login` |
| CategoryController | `/api/category` | POST `/save`, GET `/findById/{id}`, GET `/all`, PUT `/update/{id}`, DELETE `/delete/{id}` |
| CityController | `/api/city` | POST `/save`, GET `/findById/{id}`, GET `/all`, PUT `/update/{id}`, DELETE `/delete/{id}` |
| DepartmentController | `/api/department` | POST `/save`, GET `/all`, GET `/findById/{id}`, PUT `/update/{id}`, DELETE `/delete/{id}` |
| FeatureController | `/api/feature` | POST `/`, GET `/all`, GET `/{id}`, PUT `/{id}`, DELETE `/{id}` |
| HotelController | `/api/hotel` | POST `/save`, PUT `/update/{id}`, GET `/all`, DELETE `/delete/{id}`, GET `/findById/{id}`, GET `/findByHotel`, GET `/findDetailById/{id}` |
| HotelImagesController | `/api/hotel-images` | POST `/save`, DELETE `/delete/{hotImgSec}` |
| ModuleController | `/api/module` | POST `/save` |
| PaisController | `/api/country` | POST `/save`, GET `/all`, GET `/findById/{id}`, PUT `/update/{id}`, DELETE `/delete/{id}` |
| PermissionProfilesModuleController | `/api/PermissionProMod` | POST/DELETE `/{profileId}/modules/{moduleId}/submodules/{subModuleId}` |
| PermissionProfilesModulePerController | `/api/PermissionProModPer` | PATCH `/{profileId}/modules/{moduleId}/submodules/{subModuleId}/actions/{code}` |
| ProfileController | `/api/profile` | POST `/save`, PUT `/{id}`, GET `/{id}`, GET `/all`, DELETE `/{id}` |
| RbacController | `/api/profiles` | GET `/{profileId}/modules`, PATCH `/{profileId}/modules/{moduleId}/entry-allowed` |
| UserController | `/api/users` | GET `/all`, GET `/{userSec}`, GET `/findByEmail/{email}`, PUT `/{userSec}`, DELETE `/{userSec}` |
| EmailController | `/api/emails` | POST `/send-simple-email` |