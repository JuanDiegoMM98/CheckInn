package com.dam.checkinn.models;

import java.time.LocalDate;

public record UsuarioDTO(
        String nombre,
        String apellido1,
        String apellido2,
        String correo,
        String contraseña,
        String tarjetaBancaria,
        String direccion,
        LocalDate fechaNacimiento,
        String rol,
        String telefono,
        String genero
) {}
