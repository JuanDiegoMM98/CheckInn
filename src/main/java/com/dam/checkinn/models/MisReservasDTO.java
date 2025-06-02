package com.dam.checkinn.models;

import java.time.LocalDate;

public record MisReservasDTO(
        int id,
        double precio,
        boolean cancelada,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String motivoCancelacion,
        AlojamientoModel alojamiento
) {
}
