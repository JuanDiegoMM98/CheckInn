package com.dam.checkinn.models.dto.reservas;

import com.dam.checkinn.models.AlojamientoModel;

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
