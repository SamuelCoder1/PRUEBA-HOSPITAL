# Proyecto de Gestión de Citas Médicas | Medical Appointment Management Project

Este proyecto permite gestionar citas médicas, verificar la disponibilidad de los doctores, registrar el historial médico y mucho más. Está compuesto por un frontend desarrollado en Vue.js y un backend utilizando Java con Spring Boot.  
This project allows you to manage medical appointments, check doctor availability, register medical history, and much more. It consists of a frontend developed in Vue.js and a backend using Java with Spring Boot.

## Casos de Uso | Use Cases

1. **Registrar Cita Médica | Register Medical Appointment**  
   El usuario (paciente o administrador) puede registrar una cita médica, validando la disponibilidad del doctor y evitando citas duplicadas.  
   The user (patient or admin) can register a medical appointment, validating the doctor's availability and avoiding duplicate appointments.

2. **Ver Citas Registradas | View Scheduled Appointments**  
   Los usuarios (paciente, doctor, administrador) pueden ver las citas programadas, filtradas por doctor, paciente o fecha.  
   Users (patient, doctor, admin) can view scheduled appointments, filtered by doctor, patient, or date.

3. **Actualizar Cita Médica | Update Medical Appointment**  
   El usuario puede modificar los detalles de una cita ya registrada, como cambiar la hora o el doctor.  
   The user can modify the details of an already registered appointment, such as changing the time or the doctor.

4. **Cancelar Cita Médica | Cancel Medical Appointment**  
   El paciente o administrador puede cancelar una cita programada.  
   The patient or admin can cancel a scheduled appointment.

5. **Ver Historial de Citas | View Appointment History**  
   El doctor o administrador puede ver el historial de citas de un paciente, incluyendo observaciones y detalles.  
   The doctor or admin can view a patient's appointment history, including observations and details.

6. **Ver Disponibilidad del Doctor | Check Doctor Availability**  
   El paciente o administrador puede consultar la disponibilidad de un doctor para agendar una cita.  
   The patient or admin can check a doctor's availability to schedule an appointment.

7. **Registrar Historial de Citas (Observaciones) | Register Appointment History (Observations)**  
   El doctor puede agregar observaciones médicas a una cita ya realizada.  
   The doctor can add medical observations to an already completed appointment.

8. **Login de Usuario | User Login**  
   Los usuarios (paciente, doctor, administrador) inician sesión en el sistema con sus credenciales.  
   Users (patient, doctor, admin) log in to the system using their credentials.

9. **Gestión de Roles y Permisos | Role and Permission Management**  
   El administrador puede gestionar los roles y permisos de los usuarios (pacientes, doctores, administradores).  
   The admin can manage the roles and permissions of users (patients, doctors, admins).

10. **Generar Reporte de Citas | Generate Appointment Report**  
    El administrador puede generar reportes de todas las citas en un rango de fechas.  
    The admin can generate reports for all appointments within a date range.

---

## Desplegar el Proyecto desde Tu Computadora | Deploy the Project from Your Computer

### 1. Desplegar Frontend (Vue.js) | Deploy Frontend (Vue.js)
- Ingresa a la carpeta **`"hospital"`** que contiene las vistas.  
  Enter the **`"hospital"`** folder that contains the views.

- Abre una terminal en esa carpeta y ejecuta el siguiente comando:  
  Open a terminal in that folder and run the following command:

  ```bash
  code .
Luego, abre la terminal de Visual Studio Code y ejecuta el siguiente comando para iniciar el frontend:
Then, open the Visual Studio Code terminal and run the following command to start the frontend:

bash
Copiar código
npm run dev
O si prefieres otro entorno de desarrollo:
Or if you prefer another development environment:

bash
Copiar código
npm run serve
2. Desplegar Backend (Java + Spring Boot) | Deploy Backend (Java + Spring Boot)
Ingresa al directorio principal de la aplicación Java.
Go to the main directory of the Java application.

Ejecuta el proyecto en tu IDE (por ejemplo, IntelliJ IDEA o Eclipse).

Run the project in your IDE (for example, IntelliJ IDEA or Eclipse).

Una vez que el proyecto esté corriendo, dirígete a:
Once the project is running, go to:

http://localhost:8000

Credenciales de Usuario | User Credentials
A continuación se encuentran las credenciales para los usuarios predeterminados del sistema:
Below are the credentials for the default system users:

Administrador | Admin:

Document: admin
Password: admin

Doctor:

Document: doctor2
Password: contraseña

Paciente | Patient:

Document: paciente2
Password: contraseña


Requisitos para el Funcionamiento del Proyecto | Project Requirements
Backend (Java + Spring Boot)
Base de datos (MySQL) configurada en el proyecto.
Database (MySQL) configured in the project.
Frontend (Vue.js)
Vue CLI instalado.
Vue CLI installed.

¡Gracias por usar nuestro sistema de gestión de citas médicas!
Thank you for using our medical appointment management system!

CREDENCIALES DE LA BASE DE DATOS:
HOST: db-default-echeverrysamuel74-2839.g.aivencloud.com
DATABASE NAME: hospital
PORT: 20631
USER: avnadmin
PASSWORD: AVNS_B2N9nPtkcirUzKGiIQQ
