# Proyecto de Gestión de Citas Médicas

Este proyecto permite gestionar citas médicas, visualizar la disponibilidad de los doctores, registrar historial médico y mucho más. Está compuesto por un frontend en Vue.js y un backend en Java con Spring Boot.

## Casos de Uso

1. **Registrar Cita Médica**  
   El usuario (paciente o administrador) puede registrar una cita médica, validando la disponibilidad del doctor y evitando citas duplicadas.
   
2. **Ver Citas Registradas**  
   Los usuarios (paciente, doctor, administrador) pueden ver las citas programadas, filtradas por doctor, paciente o fecha.

3. **Actualizar Cita Médica**  
   El usuario puede modificar los detalles de una cita ya registrada, como cambiar la hora o el doctor.

4. **Cancelar Cita Médica**  
   El paciente o administrador puede cancelar una cita programada.

5. **Ver Historial de Citas**  
   El doctor o administrador puede ver el historial de citas de un paciente, incluyendo observaciones y detalles.

6. **Ver Disponibilidad del Doctor**  
   El paciente o administrador puede consultar la disponibilidad de un doctor para agendar una cita.

7. **Registrar Historial de Citas (Observaciones)**  
   El doctor puede agregar observaciones médicas a una cita ya realizada.

8. **Login de Usuario**  
   Los usuarios (paciente, doctor, administrador) inician sesión en el sistema con sus credenciales.

9. **Gestión de Roles y Permisos**  
   El administrador puede gestionar los roles y permisos de los usuarios (pacientes, doctores, administradores).

10. **Generar Reporte de Citas**  
    El administrador puede generar reportes de todas las citas en un rango de fechas.

---

## Desplegar el Proyecto desde Tu Computadora

### 1. Desplegar Frontend (Vue.js)
- Ingresa a la carpeta **`"hospital"`** que contiene las vistas.
- Abre una terminal en esa carpeta y ejecuta el siguiente comando:

  ```bash
  code .
Luego, abre la terminal de Visual Studio Code y ejecuta el siguiente comando para iniciar el frontend:

bash
Copiar código
npm run dev
O si prefieres otro entorno de desarrollo:

bash
Copiar código
npm run serve
2. Desplegar Backend (Java + Spring Boot)
Ingresa al directorio del main de la aplicación Java.

Ejecuta el proyecto en tu IDE (por ejemplo, IntelliJ IDEA o Eclipse).

Una vez que el proyecto esté corriendo, dirígete a:

bash
Copiar código
http://localhost:8000
Credenciales de Usuario
A continuación se encuentran las credenciales para los usuarios predeterminados del sistema:

Administrador:

Documento: admin
Contraseña: admin
Doctor:

Documento: doctor2
Contraseña: contraseña
Paciente:

Documento: paciente2
Contraseña: contraseña
Requisitos para el Funcionamiento del Proyecto
Backend (Java + Spring Boot)
JDK 8 o superior.
Spring Boot 2.x.
Base de datos (por ejemplo, MySQL o H2) configurada en el proyecto.
Frontend (Vue.js)
Node.js 16 o superior.
Vue CLI instalado.
Contribuciones
Si deseas contribuir a este proyecto, por favor sigue estos pasos:

Haz un fork del repositorio.
Crea una nueva rama para tu característica o corrección de errores.
Realiza tus cambios.
Realiza un Pull Request explicando los cambios realizados.
¡Gracias por usar nuestro sistema de gestión de citas médicas!
