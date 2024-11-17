document.addEventListener('DOMContentLoaded', function() {
    const BASE_URL = 'http://localhost:8080';

    // Token de autenticación
    const token = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiU2FtdWVsIiwicm9sZSI6IkFETUlOIiwiaWQiOjEsInN1YiI6IjEwMTMzNDE4NTIiLCJpYXQiOjE3MzE4MjUwODQsImV4cCI6MTczODgyNTA4NH0.EX8JRF6lLG4QOqa7NGy1sJ06F3c8nmkKY4z_6ZpKI-U";

    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };

    // Función para manejar respuestas exitosas
    function handleResponse(response) {
        if (response.ok) {
            return response.json();
        } else {
            console.error(`Error en la solicitud: ${response.status} - ${response.statusText}`);
            throw new Error('Error en la solicitud: ' + response.statusText);
        }
    }

    // Función para manejar errores
    function handleError(error) {
        console.error(error);
        alert('Ha ocurrido un error: ' + error.message);
    }

    // Cargar doctores
    function loadDoctors() {
        fetch(`${BASE_URL}/doctors`, {
            method: 'GET',
            headers: headers
        })
        .then(response => handleResponse(response))
        .then(doctors => {
            const doctorTableBody = document.getElementById('doctorTable').getElementsByTagName('tbody')[0];
            const doctorSelect = document.getElementById('doctorId');
            
            doctorTableBody.innerHTML = '';
            doctorSelect.innerHTML = '';

            doctors.forEach((doctor, index) => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${index + 1}</td>
                    <td>${doctor.user.name}</td>
                    <td>${doctor.user.document}</td>
                    <td>${doctor.status}</td>
                    <td>${doctor.phoneNumber}</td>
                    <td><button class="btn btn-info select-doctor" data-id="${doctor.id}">Seleccionar</button></td>
                `;
                doctorTableBody.appendChild(row);
  
                const option = document.createElement('option');
                option.value = doctor.id;
                option.textContent = `${doctor.user.name} (${doctor.user.document})`;
                doctorSelect.appendChild(option);
            });

            // Asignar evento para seleccionar doctor
            const selectButtons = document.querySelectorAll('.select-doctor');
            selectButtons.forEach(button => {
                button.addEventListener('click', function() {
                    const doctorId = this.getAttribute('data-id');
                    document.getElementById('doctorId').value = doctorId;
                    loadTimeSlots(doctorId); // Cargar horarios para el doctor seleccionado
                });
            });
        })
        .catch(handleError);
    }

    // Cargar pacientes
    function loadPatients() {
        fetch(`${BASE_URL}/patients`, {
            method: 'GET',
            headers: headers
        })
        .then(response => handleResponse(response))
        .then(patients => {
            const patientTableBody = document.getElementById('patientTable').getElementsByTagName('tbody')[0];
            const patientSelect = document.getElementById('patientId');
            
            patientTableBody.innerHTML = '';
            patientSelect.innerHTML = '';

            patients.forEach((patient, index) => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${index + 1}</td>
                    <td>${patient.user.name}</td>
                    <td>${patient.user.document}</td>
                    <td>${patient.address}</td>
                    <td>${patient.phoneNumber}</td>
                    <td>${patient.dateOfBirth}</td>
                    <td><button class="btn btn-info select-patient" data-id="${patient.id}">Seleccionar</button></td>
                `;
                patientTableBody.appendChild(row);

                const option = document.createElement('option');
                option.value = patient.id;
                option.textContent = `${patient.user.name} (${patient.user.document})`;
                patientSelect.appendChild(option);
            });

            const selectButtons = document.querySelectorAll('.select-patient');
            selectButtons.forEach(btn => {
                btn.addEventListener('click', function() {
                    const patientId = this.getAttribute('data-id');
                    document.getElementById('patientId').value = patientId;
                });
            });
        })
        .catch(handleError);
    }

    // Cargar horarios
    function loadTimeSlots(doctorId) {
        const dateInput = document.getElementById('appointmentDate');
        const date = dateInput ? dateInput.value : '2024-11-18';  // Usa la fecha del input o una fecha por defecto

        fetch(`${BASE_URL}/appointments/available-slots/${doctorId}?date=${date}`, {
            method: 'GET',
            headers: headers
        })
        .then(response => {
            console.log("URL solicitada:", `${BASE_URL}/appointments/available-slots/${doctorId}?date=${date}`);
            return handleResponse(response);
        })
        .then(timeSlots => {
            const timeSlotsList = document.getElementById('timeSlots');
            timeSlotsList.innerHTML = '';

            if (timeSlots.length === 0) {
                const noSlotsItem = document.createElement('li');
                noSlotsItem.classList.add('list-group-item');
                noSlotsItem.textContent = 'No hay horarios disponibles para esta fecha.';
                timeSlotsList.appendChild(noSlotsItem);
                return;
            }

            // Mostrar horarios disponibles
            timeSlots.forEach(slot => {
                const listItem = document.createElement('li');
                listItem.classList.add('list-group-item');
                listItem.textContent = `Horario: ${slot}`;

                // Marcar horarios ocupados
                listItem.style.backgroundColor = '#ffffff';  // Default white color

                // Si el horario está ocupado, cambiar color
                if (isSlotBooked(doctorId, slot, date)) {
                    listItem.style.backgroundColor = '#f8d7da'; // Rojo para citas ocupadas
                    listItem.style.color = '#721c24';
                    listItem.textContent = `Horario: ${slot} (Ocupado)`;
                }

                timeSlotsList.appendChild(listItem);
            });
        })
        .catch(handleError);
    }

    // Función para verificar si un horario está reservado (esto es solo un ejemplo, se puede personalizar según la lógica de tu backend)
    function isSlotBooked(doctorId, slot, date) {
        // Lógica para verificar si el horario está reservado
        // Esto debería hacer una solicitud al servidor para verificar si el horario está ocupado.
        // A continuación un ejemplo simple:
        return false; // Por defecto, todos los horarios están disponibles
    }

    // Crear cita
    document.getElementById('createAppointmentBtn').addEventListener('click', function() {
        const doctorId = document.getElementById('doctorId').value;
        const patientId = document.getElementById('patientId').value;
        const appointmentDate = document.getElementById('appointmentDate').value;

        if (!doctorId || !patientId || !appointmentDate) {
            alert('Por favor complete todos los campos.');
            return;
        }

        const appointmentData = {
            doctorId: doctorId,
            patientId: patientId,
            date: appointmentDate
        };

        fetch(`${BASE_URL}/appointments`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(appointmentData)
        })
        .then(response => handleResponse(response))
        .then(data => {
            alert('Cita creada con éxito');
            loadDoctors();  // Recargar doctores
            loadPatients();  // Recargar pacientes
        })
        .catch(handleError);
    });

    // Inicializar la página
    loadDoctors();
    loadPatients();
});
