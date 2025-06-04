package com.dam.checkinn.models.dto.reservas;

import com.dam.checkinn.models.AlojamientoModel;

import java.time.LocalDate;

public record MisReservasDTO(
        int idReserva,
        double precio,
        boolean cancelada,
        boolean valorada,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String motivoCancelacion,
        int idAlojamiento,
        byte[] imagen,
        String nombre,
        String direccion,
        int capacidad
) {
}
