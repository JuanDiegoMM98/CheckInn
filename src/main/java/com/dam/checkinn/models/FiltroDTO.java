package com.dam.checkinn.models;

import java.time.LocalDate;
import java.util.List;

public record FiltroDTO(
        String dni,
        String provincia,
        Double valoracionMinima,
        Double precioMaximo,
        List<AlojamientoModel.Servicio> servicios,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer personasMaximas
) {}

