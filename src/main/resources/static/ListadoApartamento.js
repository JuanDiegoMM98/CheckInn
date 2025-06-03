
axios.get('/api/usuario/estancias') // Cambia por tu endpoint real
  .then(function(response) {
    const { esPro, estancias } = response.data;

    const proMessage = document.getElementById('pro-message');
    const container = document.getElementById('estancias-container');
    const mensajeVacio = document.getElementById('sin-estancias');

    // Si el usuario NO es PRO, mostramos el mensaje
    if (!esPro) {
      proMessage.style.display = 'block';
    }

    // Si no hay estancias, mostramos mensaje vacío
    if (estancias.length === 0) {
      mensajeVacio.style.display = 'block';
      return;
    }

    estancias.forEach(estancia => {
      const card = document.createElement('div');
      card.className = 'card';

      card.innerHTML = `
        <img src="${estancia.imagen}" alt="Estancia ${estancia.nombre}">
        <div class="card-body">
          <h3>${estancia.nombre}</h3>
          <p>${estancia.ubicacion}</p>
          <p><strong>${estancia.precio} €/noche</strong></p>
        </div>
      `;

      container.appendChild(card);
    });
  })
  .catch(function(error) {
    console.error('Error al obtener estancias:', error);
  });