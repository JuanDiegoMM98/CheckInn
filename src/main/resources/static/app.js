const uri = "http://localhost:5000/api/v1";

const mensajeErrorDatosUsuario = "Error al cargar los datos del usuario";

function RanderizarBaseImagen64(base64Data) {
    if (!base64Data || typeof base64Data !== 'string') {
        return null;
    }

    return `data:image/jpeg;base64,${base64Data}`;
}

function CrearImagenBase64(base64Data) {
    if (!base64Data || typeof base64Data !== 'string') {
        return `<div class="w-full h-44 bg-gray-200 flex items-center justify-center text-gray-500">Sin imagen</div>`;
    }

    return `<img src="data:image/jpeg;base64,${base64Data}" class="w-full h-44 object-cover rounded-t-2xl" alt="Imagen alojamiento" />`;
}

function irAreaPersonal() {
    window.location.href = "AreaPersonal.html"
}

async function cerrarSesion() {
    const responseLogout = await axios.post("/logout");
    if (responseLogout.status === 302 || responseLogout.status === 200) {
        window.location.href = "Login.html"
    } else {
        alert("Error al cerrar sesión")
    }
}