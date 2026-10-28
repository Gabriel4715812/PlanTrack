# PlanTrack

**PlanTrack** es un prototipo web desarrollado como parte del Trabajo Final de Grado de la carrera Licenciatura en Informática. El sistema está orientado a la gestión, consulta y trazabilidad de planos técnicos e industriales dentro de una organización.

El objetivo principal del proyecto es centralizar la documentación técnica, facilitar el acceso rápido a los planos, controlar versiones y permitir la consulta mediante códigos QR.

---
Video explicativo sobre la Demo: https://youtu.be/jB7se7fe9kI
---

## Funcionalidades principales

* Inicio de sesión con autenticación de usuarios.
* Registro de usuarios.
* Gestión de roles: Administrador, Diseñador y Operario.
* Gestión de sectores industriales.
* Gestión de equipos asociados a sectores.
* Gestión de piezas asociadas a equipos.
* Gestión de posiciones técnicas asociadas a piezas.
* Carga y administración de planos técnicos.
* Asociación de planos a posiciones específicas.
* Carga, visualización y descarga de archivos de planos.
* Control de versiones de planos.
* Historial de versiones por plano.
* Generación de códigos QR asociados a planos.
* Acceso rápido al detalle del plano mediante QR.
* Restricción de funciones según el rol del usuario.

---

## Roles del sistema

### Administrador

Puede acceder a todas las funcionalidades del sistema:

* Gestionar usuarios.
* Gestionar sectores.
* Gestionar equipos.
* Gestionar piezas.
* Gestionar posiciones.
* Gestionar planos.
* Gestionar versiones.
* Generar códigos QR.

### Diseñador

Puede administrar la estructura técnica y documental:

* Gestionar sectores.
* Gestionar equipos.
* Gestionar piezas.
* Gestionar posiciones.
* Gestionar planos.
* Cargar nuevas versiones.
* Generar códigos QR.

No puede gestionar usuarios.

### Operario

Puede consultar información técnica:

* Consultar planos.
* Visualizar archivos.
* Descargar planos.
* Consultar versiones.
* Visualizar códigos QR.

No puede crear, editar ni eliminar registros administrativos.

---

## Tecnologías utilizadas

* Java 17+
* Spring Boot 3.3.1
* Spring Web
* Spring Security
* Spring Data JPA
* Thymeleaf
* Thymeleaf Layout Dialect
* Thymeleaf Extras Spring Security
* MySQL
* Bootstrap 5
* ZXing para generación de códigos QR
* Maven
* Git y GitHub

---

## Estructura general del sistema

La estructura funcional del sistema sigue la siguiente jerarquía:

```text
Sector
   └── Equipo
         └── Pieza
               └── Posición
                     └── Plano
                           └── Versión
                                 └── Código QR
```

---

## Módulos implementados

```text
src/main/java/com/plantrack
│
├── config
│   ├── SecurityConfig.java
│   └── DemoDataInitializer.java
│
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   ├── SectorController.java
│   ├── EquipmentController.java
│   ├── PartController.java
│   ├── PositionController.java
│   ├── FlatController.java
│   ├── FlatVersionController.java
│   └── QrCodeController.java
│
├── dto
│
├── model
│
├── repository
│
└── service
```

---

## Requisitos previos

Antes de ejecutar el proyecto, se debe tener instalado:

* Java 17 o superior.
* Maven.
* MySQL Server.
* Visual Studio Code, IntelliJ IDEA, Eclipse u otro IDE compatible con Java.
* Git.

---

## Configuración de la base de datos

Crear una base de datos en MySQL con el nombre:

```sql
CREATE DATABASE plantrack_db;
```

Luego configurar el archivo:

```text
src/main/resources/application.properties
```

Ejemplo de configuración:

```properties
spring.application.name=plantrack

server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/plantrack_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.thymeleaf.cache=false

spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB

plantrack.upload-dir=uploads/flats
plantrack.version-upload-dir=uploads/versions
plantrack.qr-upload-dir=uploads/qr
```

---

## Ejecución del proyecto

Desde la raíz del proyecto, ejecutar:

```bash
mvn spring-boot:run
```

Luego ingresar en el navegador a:

```text
http://localhost:8080
```

o directamente a:

```text
http://localhost:8080/login
```

---

## Usuarios de prueba

El sistema cuenta con usuarios demo para probar los distintos roles:

```text
Administrador
Usuario: admin
Contraseña: Admin1234

Diseñador
Usuario: designer
Contraseña: Demo1234

Operario
Usuario: operator
Contraseña: Demo1234
```

---

## Flujo principal de uso

1. Iniciar sesión en el sistema.
2. Crear o seleccionar un sector.
3. Registrar un equipo asociado al sector.
4. Registrar una pieza asociada al equipo.
5. Registrar una posición asociada a la pieza.
6. Cargar un plano asociado a la posición.
7. Visualizar o descargar el archivo del plano.
8. Registrar una nueva versión del plano.
9. Consultar el historial de versiones.
10. Generar un código QR para acceder rápidamente al plano.

---

## Carga de archivos

Los archivos cargados por el sistema se almacenan localmente en las siguientes carpetas:

```text
uploads/flats
uploads/versions
uploads/qr
```

Estas carpetas no se suben al repositorio porque pueden contener archivos locales, pruebas o documentación técnica cargada por el usuario.

---

## Estado del prototipo

El prototipo se encuentra funcional e incluye los módulos principales necesarios para demostrar la gestión y trazabilidad de planos técnicos e industriales.

El sistema permite evidenciar:

* Centralización de planos.
* Control de acceso por rol.
* Organización jerárquica de documentación técnica.
* Carga y descarga de archivos.
* Control de versiones.
* Acceso rápido mediante códigos QR.

---

## Autor

**Guillermo Gabriel Noriega**

Proyecto desarrollado para el Trabajo Final de Grado de la carrera Licenciatura en Informática.

---

## Licencia

Este proyecto fue desarrollado con fines académicos.
