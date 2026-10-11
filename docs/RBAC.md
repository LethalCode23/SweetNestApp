# Arquitectura de Permisos — RBAC (SweetNest)

> Documento complementario a [API.md](../API.md). Aquí se explica **cómo funciona** el control de acceso;
> para ver los endpoints con ejemplos cURL, consulta la referencia de la API.

## ¿Qué es RBAC en SweetNest?

**RBAC** (*Role-Based Access Control*) es el modelo de control de acceso basado en roles. En SweetNest,
cada usuario tiene un **perfil** (rol), y cada perfil tiene permisos sobre una jerarquía de
**módulos → submódulos → acciones**. El frontend consulta estos permisos al iniciar sesión para
renderizar el menú y los botones disponibles.

## Jerarquía de permisos

```
User (usuario)
 └── Profile (perfil / rol)
      │   ¿Tiene control de acceso? → hasControlAccess ('S'/'N')
      │
      └── PermissionProfiles (perfil + módulo)
      │        ¿Puede ENTRAR al módulo? → entryAllowed ('S'/'N')
      │
      └── PermissionProfilesModule (perfil + módulo + submódulo)
      │        ¿Tiene acceso al submódulo?
      │
      └── PermissionProfilesModulePer (perfil + módulo + submódulo + acción)
               ¿Puede ejecutar la acción? → check ('S'/'N')
               Acciones: R (Listar) · C (Agregar) · U (Editar) · D (Borrar)
```

**Regla de oro:** los permisos se heredan hacia abajo. Sin acceso al módulo no hay acceso a sus
submódulos; sin acceso al submódulo no se evalúan las acciones.

## Entidades y tablas

| Entidad (JPA) | Tabla | Clave | Campos principales |
| --- | --- | --- | --- |
| `Profile` | `profile` | `id` | `name`, `state`, `is_default`, `has_control_access` |
| `Module` | `modules` | `id` | `name`, `url`, `description`, `state` |
| `SubModule` | `submodules` | `id` | `module_id` (FK), `name`, `url`, `description`, `state` |
| `PermissionProfiles` | `permission_profiles` | compuesta: `profile_id` + `module_id` | `entry_allowed` |
| `PermissionProfilesModule` | `permission_profiles_module` | compuesta: `profile_id` + `module_id` + `submodule_id` | — |
| `PermissionProfilesModulePer` | `permission_profiles_module_per` | compuesta: `profile_id` + `module_id` + `submodule_id` + acción | `action_name`, `action_description`, `check` |

Las tres tablas de permisos usan **claves primarias compuestas** (`@EmbeddedId`), lo que garantiza
que un mismo par/trío/cuádruple de permiso no se duplique.

## Flujo de autenticación y autorización

1. El usuario hace login en `POST /api/auth/login` y recibe un **JWT**.
2. El token contiene la información del usuario, incluido su **perfil**.
3. El frontend llama a `GET /api/profiles/{profileId}/modules` con el token.
4. El backend responde el árbol de módulos → submódulos → acciones permitidos para ese perfil.
5. El frontend renderiza **solo** el menú y las acciones que el perfil tiene habilitadas.

```
Login (JWT) ──▶ GET /api/profiles/{profileId}/modules ──▶ Menú y acciones del usuario
```

## Mapa de endpoints por nivel de permiso

| Nivel | Endpoint | Método | Descripción |
| --- | --- | --- | --- |
| Consulta del menú | `/api/profiles/{profileId}/modules` | GET | Devuelve el árbol de permisos del perfil |
| Módulo (entrada) | `/api/profiles/{profileId}/modules/{moduleId}/entry-allowed` | PATCH | Habilita/deshabilita el acceso al módulo |
| Submódulo | `/api/PermissionProMod/{profileId}/modules/{moduleId}/submodules/{subModuleId}` | POST | Otorga acceso al submódulo |
| Submódulo | `/api/PermissionProMod/{profileId}/modules/{moduleId}/submodules/{subModuleId}` | DELETE | Revoca acceso al submódulo |
| Acción | `/api/PermissionProModPer/{profileId}/modules/{moduleId}/submodules/{subModuleId}/actions/{code}` | PATCH | Habilita/deshabilita una acción (R/C/U/D) |

> Los ejemplos cURL y las respuestas completas de estos endpoints están en
> [API.md](../API.md#13-rbaccontroller--apiprofiles).

## Datos semilla (DataInitializer)

Al arrancar, la clase `src/main/java/com/dh/demo/config/DataInitializer.java` puebla la base de datos
en memoria con todos los datos de ejemplo, **solo si la base está vacía**. Esto permite probar el
sistema completo sin cargar nada a mano.

### Perfiles (roles)

| Perfil | `state` | `isDefault` | `hasControlAccess` | Acceso |
| --- | --- | --- | --- | --- |
| Administrador | Activo | No | **Sí** | Todos los módulos con CRUD completo (excepto "Mi Cuenta", solo Editar) |
| Usuario | Activo | **Sí** | No | Solo el módulo "Mi Cuenta" con acción Editar (U) |

### Módulos y submódulos

| Módulo | URL | Submódulos | Perfil asignado |
| --- | --- | --- | --- |
| Hoteles | `/hotels` | Hoteles | Administrador |
| Catálogos | `/catalog` | Países, Departamentos, Ciudades, Categorías, Características | Administrador |
| Usuarios y Seguridad | `/security` | Usuarios | Administrador |
| Mi Cuenta | `/userProfile` | Mi Perfil | Administrador y Usuario |
| Panel de control | `/profiles` | Perfiles | Administrador |

Los permisos por acción se crean con `FULL_CRUD` (R, C, U, D), salvo "Mi Cuenta" que solo recibe
la acción **U** (Editar) para ambos perfiles.

### Usuarios de prueba

| Email | Contraseña | Perfil |
| --- | --- | --- |
| `admin@sweetnest.com` | `Admin123` | Administrador |
| `usuario@sweetnest.com` | `Usuario123` | Usuario |

### Maestras geográficas y catálogos

| Maestra | Registros | Detalle |
| --- | --- | --- |
| Países | 3 | Colombia, Argentina, México |
| Departamentos | 9 | 5 en Colombia, 2 en Argentina, 2 en México |
| Ciudades | 10 | 6 en Colombia, 2 en Argentina, 2 en México |
| Categorías | 3 | Playa, Boutique, Económico |
| Características | 3 | Piscina, Wi-Fi, Parqueadero |
| Hoteles | 10 | Ejemplos distribuidos en las ciudades sembradas |

### Verificación en consola H2

Puedes verificar estos datos en la consola H2 (`http://localhost:8080/h2-console`,
JDBC URL `jdbc:h2:mem:SweetNestDB`, usuario `sa`, contraseña `sa`) consultando las tablas
`profile`, `modules`, `submodules`, `permission_profiles`, `permission_profiles_module` y
`permission_profiles_module_per`.

---

Volver a [README](../README.md) · [API.md](../API.md)
