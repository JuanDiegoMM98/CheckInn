package com.dam.checkinn.models.dto.reservas;
import com.dam.checkinn.models.AlojamientoModel;

import java.time.LocalDate;

public record CrearActualizarReservaDTOFront(
        int idUsuario, // Necesario para crear
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

