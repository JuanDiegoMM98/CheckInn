const uri = "http://localhost:5000/api/v1";

function obtener_session() {
    let nombre = sessionStorage.getItem("nombre");
    console.log(nombre);
}

function guardar_session(dataArray) {
    let usuario = dataArray;

    /*
    Formato de dataArray:

    {
        "dni": "String",
        "nombre": "String",
        "apellido1": "String",
        "apellido2": "String",
        "correo": "String",
        "contraseña": "String",
        "tarjetaBancaria": "String",
        "direccion": "String",
        "fechaNacimiento": "String",
        "telefono": "String",       // OPCIONAL
        "genero": "String"          // OPCIONAL
    }
    */

    sessionStorage.setItem("nombre", JSON.stringify(usuario));
}
