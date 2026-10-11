# SweetNest - Hotel Booking System (Backend)

![Logo SweetNest](https://raw.githubusercontent.com/LethalCode23/SweetNestUI/main/src/assets/SweetNestLogo.ico)

Backend REST para **SweetNest**, un sistema de reservas de alojamiento. API en Spring Boot con autenticación JWT, gestión de hoteles (imágenes, categorías, características), maestras geográficas (países, departamentos, ciudades) y control de acceso basado en roles (RBAC).

## Acerca del proyecto

SweetNest es una herramienta que optimiza y agiliza el proceso de reservas para alojamiento con su interfaz intuitiva, donde el usuario podrá navegar en búsqueda de las mejores opciones para alojarse en su destino. Permite conectar a los usuarios con las mejores ofertas de alojamiento que están disponibles actualmente.

La solución consiste en una aplicación web de arquitectura cliente-servidor. El alcance funcional de la primera etapa (Sprint 1) incluye:

- **Interfaz intuitiva:** layout responsivo con encabezado fijo (sticky header) que facilita la navegación constante y el acceso a las funciones de usuario.
- **Gestión de inventario:** módulo administrativo para el registro de productos/habitaciones, con validaciones de servidor para evitar nombres duplicados y asegurar la integridad de la base de datos.
- **Catálogo inteligente:** motor de visualización en el home que muestra hasta 10 productos de forma aleatoria y sin repeticiones, organizados en una cuadrícula de 2 columnas por 5 filas.
- **Detalle de producto:** sistema de rutas dinámicas para visualizar la información específica, descripción e imágenes de cada alojamiento seleccionado.

### Propuesta de valor técnica

- **Escalabilidad:** separación de responsabilidades con arquitectura cliente-servidor, lo que facilita las actualizaciones futuras del frontend o del backend.
- **Código limpio:** componentes modulares para facilitar el mantenimiento a largo plazo.
- **Persistencia robusta:** manejo de transacciones seguras y validaciones, tanto en la capa del cliente como en la del servicio.

## Stack tecnológico

| Capa | Tecnología |
| --- | --- |
| Lenguaje | Java 17 |
| Framework | Spring Boot 4 (Web, Data JPA, Security, Validation, Mail) |
| Autenticación | JWT (jjwt) |
| Base de datos | H2 en memoria (`SweetNestDB`) |
| Documentación API | SpringDoc OpenAPI — Swagger UI |
| Build | Maven Wrapper (`mvnw`) |
| Frontend | React + JavaScript + Vite → repositorio [SweetNestUI](https://github.com/LethalCode23/SweetNestUI) |

## Requisitos previos

- **Java 17 o superior** — verifica con `java -version`
- **Git**
- No se requiere base de datos externa ni variables de entorno para arrancar (la BD H2 se crea y se puebla en memoria al iniciar)

## Guía de instalación y ejecución

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/LethalCode23/SweetNestApp.git
   cd SweetNestApp
   ```

2. **Ejecutar el backend:**

   ```bash
   # Windows
   ./mvnw.cmd spring-boot:run

   # macOS / Linux
   ./mvnw spring-boot:run
   ```

3. **Verificar que esté corriendo:** abre Swagger UI en <http://localhost:8080/swagger-ui.html>

El servidor queda disponible en `http://localhost:8080`. Al arrancar, la clase `DataInitializer` puebla la base de datos con datos semilla (maestras y usuarios de prueba).

### Datos de acceso (usuarios seed)

| Email | Contraseña | Perfil |
| --- | --- | --- |
| `admin@sweetnest.com` | `Admin123` | Administrador |
| `usuario@sweetnest.com` | `Usuario123` | Usuario |

### Consola H2

- URL: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:SweetNestDB`
- Usuario: `sa` — Contraseña: `sa`

### Notas

- **CORS:** el backend solo acepta peticiones desde `http://localhost:5173` (el puerto del frontend Vite en desarrollo).
- **⚠️ Credenciales SMTP:** el envío de correos usa Gmail SMTP con credenciales hardcodeadas en `src/main/resources/application.properties`. Se recomienda moverlas a variables de entorno antes de cualquier despliegue fuera de desarrollo.

## Documentación de la API

- **[API.md](./API.md)** — los 53 endpoints documentados con ejemplos de respuesta y **cURL** para probar cada uno (incluye el flujo completo de login JWT).
- **Swagger UI** (interfaz interactiva): <http://localhost:8080/swagger-ui.html>
- **Spec OpenAPI** (JSON): <http://localhost:8080/v3/api-docs>

## Estructura del proyecto

```
src/main/java/com/dh/demo/
├── authentication/   # AuthController, peticiones/respuestas de login
├── config/           # Seguridad (JWT), CORS, archivos, datos semilla
├── controller/       # Controladores REST
├── dto/              # DTOs de entrada (request/) y salida (response/)
├── entity/           # Entidades JPA
├── exception/        # Manejadores y excepciones de error
├── mapper/           # Conversión entity <-> dto
├── openApi/          # Configuración de Swagger/OpenAPI
├── repository/       # Repositorios Spring Data JPA
├── service/          # Lógica de negocio (interfaces + impl/)
└── validator/        # Validaciones de negocio
```

## Pruebas

### Backend — [PLAN_DE_PRUEBAS.md](./PLAN_DE_PRUEBAS.md)

Plan de pruebas manuales de la API ejecutado con cURL: 27 casos cubriendo autenticación, autorización, CRUD, hoteles, imágenes, RBAC y correos (25 pass / 2 anomalías detectadas).

### Frontend — Sprint 1

Se diseñaron y ejecutaron pruebas basadas en las historias de usuario del Sprint 1. Las pruebas fueron realizadas de forma manual en el entorno de desarrollo, validando la persistencia de los datos en la base de datos en memoria y la correcta comunicación entre el frontend (React) y el backend (Spring Boot).

| ID | Escenario de prueba | Resultado esperado | Estado |
| :--- | :--- | :--- | :--- |
| **TC-01** | Persistencia del Header al hacer scroll. | El header permanece fijo en la parte superior. | ✅ Pass |
| **TC-02** | Visualización en dispositivos móviles. | Diseño 100% responsivo sin errores visuales. | ✅ Pass |
| **TC-03** | Registro de producto con nombre duplicado. | El sistema muestra alerta de error y bloquea el registro. | ✅ Pass |
| **TC-04** | Aleatoriedad del catálogo en el Home. | Los productos cambian de orden al recargar y no se repiten. | ✅ Pass |

## Diseño

### Logotipo

![Logo SweetNest](https://raw.githubusercontent.com/LethalCode23/SweetNestUI/main/src/assets/SweetNestLogo.ico)

### Paleta de colores

![Paleta de Colores](https://raw.githubusercontent.com/LethalCode23/SweetNestUI/main/src/assets/PaletadeColores.png)
