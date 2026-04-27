# SweetNest - Hotel Booking System

# 1. Definición:
SweetNest es una herramienta que optimiza y agilizará el proceso de reservas para alojamiento con su interfaz intuitiva, donde el usuario podrá navegar en búsqueda de las mejores opciones para alojarse en su destino. Permite conectar a los usuarios con las mejores ofertas de alojamiento que están disponibles actualmente.

# 2. Solucion a desarrollar:
Nuestra solución consiste en una aplicación web de arquitectura cliente-servidor, que implementa las siguientes funcionalidades en su primera etapa de desarrollo:

  1. Alcance Funcional (Spring 1)
     
     Interfaz Intuitiva:
     Implementación de un layout responsivo con un encabezado fijo (Sticky Header) que facilita la navegación constante y el acceso a las funciones de usuario.
     
     Gestión de Inventario:
     Módulo administrativo para el registro de productos/habitaciones, incluyendo validaciones de servidor para evitar nombres duplicados y asegurar la integridad de la base de datos.
     
     Catálogo Inteligente:
     Motor de visualización en el home que muestra hasta 10 productos de forma aleatoria y sin repeticiones, organizado en una cuadrícula de 2 columnas por 5 filas.
     
     Detalle de Producto:
     Sistema de rutas dinámicas para visualizar la información específica, descripción e imágenes de cada alojamiento seleccionado.

# Propuesta de Valor Tecnica

  Escalabilidad:
  Separación de responsabilidades ya que se usó una arquitectura de cliente-servidor, lo que facilita las actualizaciones futuras del frontend o del backend.
  
  Código limpio:
  Componentes modulares para facilitar el mantenimiento a largo plazo.
  
  Persistencia robusta:
  Manejo de transacciones seguras y validaciones, tanto en la capa del cliente como en la del servicio.

# Guía de instalación
  El proyecto cuenta con una base de datos en memoria, lo que facilita la ejecución del proyecto. El frontend está desarrollado en React con JavaScript y Vite, el backend está construido 
  con Spring Boot y Java.

# Planificación y ejecución de los tests
  Se diseñaron y ejecutaron pruebas basadas en las historias de usuario del Sprint 1.

  # Plan de pruebas
  | ID | Escenario de prueba | Resultado esperado | Estado |
  | :--- | :--- | :--- | :--- |
  | **TC-01** | Persistencia del Header al hacer scroll. | El header permanece fijo en la parte superior. | ✅ Pass |
  | **TC-02** | Visualización en dispositivos móviles. | Diseño 100% responsivo sin errores visuales. | ✅ Pass |
  | **TC-03** | Registro de producto con nombre duplicado. | El sistema muestra alerta de error y bloquea el registro. | ✅ Pass |
  | **TC-04** | Aleatoriedad del catálogo en el Home. | Los productos cambian de orden al recargar y no se repiten. | ✅ Pass |
  
  # Evidencia de ejecución
  Las pruebas fueron realizadas de forma manual en el entorno de desarrollo, validando la persistencia de los datos en la base de datos en memoria y la correcta comunicación entre el frontend (React) y el backend (Spring Boot).

# Logotipo
![Logo SweetNest](https://raw.githubusercontent.com/LethalCode23/SweetNestUI/main/src/assets/SweetNestLogo.ico)

# Paleta de Colores
![Paleta de Colores](https://raw.githubusercontent.com/LethalCode23/SweetNestUI/main/src/assets/PaletadeColores.png)
