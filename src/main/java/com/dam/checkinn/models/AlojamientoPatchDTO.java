package com.dam.checkinn.models;

import java.time.LocalDate;
import java.util.List;

public record AlojamientoPatchDTO(
        String nombre,
        String descripcion,
        String provincia,
        double precioNoche,
        int capacidad,
        byte[] imagen,
        List<AlojamientoModel.Servicio> servicios,
        LocalDate inicioBloqueo,
        LocalDate finBloqueo
) {}