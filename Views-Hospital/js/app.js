document.addEventListener('DOMContentLoaded', function() {
  // URL base del backend
  const BASE_URL = 'http://localhost:8080';

  // Obtener el token JWT desde localStorage o donde lo almacenes
  const token = localStorage.getItem('token');

  // Configuración de las cabeceras para las solicitudes con el token
  const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
  };

  // Función para manejar la respuesta de la API
  function handleResponse(response) {
      if (response.ok) {
          return response.json();
      } else {
          throw new Error('Error en la solicitud: ' + response.statusText);
      }
  }

  // Función para manejar errores
  function handleError(error) {
      console.error(error);
      alert('Ha ocurrido un error: ' + error.message);
  }

  // Función para cargar los doctores
  function loadDoctors() {
      fetch(`${BASE_URL}/doctors`, {
          method: 'GET',
          headers: headers
      })
      .then(response => {
          return handleResponse(response);
      })
      .then(doctors => {
          console.log('Doctores:', doctors);
          // Aquí se agregan los doctores a la lista del HTML
          const doctorList = document.getElementById('doctorList');
          const doctorSelect = document.getElementById('doctorId');
          doctorList.innerHTML = ''; // Limpiar la lista
          doctorSelect.innerHTML = ''; // Limpiar el select del formulario

          // Mostrar doctores en la lista y en el formulario de cita
          doctors.forEach(doctor => {
              const doctorItem = document.createElement('li');
              doctorItem.classList.add('list-group-item');
              doctorItem.textContent = `${doctor.name} (${doctor.specialty})`;
              doctorList.appendChild(doctorItem);

              const option = document.createElement('option');
              option.value = doctor.id;
              option.textContent = `${doctor.name} (${doctor.specialty})`;
              doctorSelect.appendChild(option);
          });
      })
      .catch(handleError);
  }

  // Función para crear una cita
  function createAppointment(appointmentData) {
      fetch(`${BASE_URL}/appointments/create`, {
          method: 'POST',
          headers: headers,
          body: JSON.stringify(appointmentData)
      })
      .then(response => {
          return handleResponse(response);
      })
      .then(appointment => {
          console.log('Cita creada:', appointment);
          alert('Cita creada con éxito');
      })
      .catch(handleError);
  }

  // Llamada a la función para cargar los doctores al iniciar
  loadDoctors();

  // Event listener para crear la cita
  document.getElementById('createAppointmentBtn').addEventListener('click', function() {
      const doctorId = document.getElementById('doctorId').value;
      const patientId = document.getElementById('patientId').value;
      const appointmentDate = document.getElementById('appointmentDate').value;

      if (!doctorId || !patientId || !appointmentDate) {
          alert('Por favor, completa todos los campos');
          return;
      }

      const appointmentData = {
          doctorId: doctorId,
          patientId: patientId,
          appointmentDate: appointmentDate
      };

      createAppointment(appointmentData);
  });
});
