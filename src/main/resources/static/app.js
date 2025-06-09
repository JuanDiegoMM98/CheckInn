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

function ficheroAImagenBase64(file) {
    return new Promise((resolve, reject) => {
        // 1) Validar tipo Imagen
        if (!file.type.startsWith('image/')) {
            return reject(new Error('El fichero no es una imagen.'));
        }

        const reader = new FileReader();

        reader.onload = () => {
            // reader.result es algo como "data:image/png;base64,iVBORw0KGgoAAAANS..."
            // Si solo quieres la parte base64, quitas el prefijo:
            const base64String = reader.result
                .toString()
                .split(',')[1];
            resolve(base64String);
        };

        reader.onerror = error => reject(error);

        reader.readAsDataURL(file);
    });
}