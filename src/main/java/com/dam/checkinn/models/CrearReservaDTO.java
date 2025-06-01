package com.dam.checkinn.models;
import java.time.LocalDate;

public record CrearReservaDTO(
        String dni, // Necesario para crear
        int idReserva, // Necesario para actualizaciones
        int idAlojamiento, // Necesario para actualizaciones
        LocalDate fechaInicio, // Necesario para crear
        LocalDate fechaFin, // Necesario para crear
        boolean cancelada, // Necesario para actualizaciones
        double valoracion, // Necesario para actualizaciones
        String motivoCancelacion,// Necesario para actualizaciones
        AlojamientoModel alojamiento
) {
}

