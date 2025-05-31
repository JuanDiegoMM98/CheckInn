package com.dam.checkinn.models;
import java.time.LocalDate;

public record CrearReservaDTO(
        String dni,
        int idAlojamiento,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean cancelada,
        String motivoCancelacion
) {
}

