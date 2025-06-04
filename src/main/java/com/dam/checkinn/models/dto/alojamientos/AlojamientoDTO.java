package com.dam.checkinn.models.dto.alojamientos;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.ReservaModel;

import java.time.LocalDate;
import java.util.List;

public record AlojamientoDTO(
        String nombre,
        String descripcion,
        String provincia,
        String direccion,
        double precioNoche,
        double valoracionMedia,
        int capacidad,
        byte[] imagen,
        List<AlojamientoModel.Servicio> servicios,
        LocalDate inicioBloqueo,
        LocalDate finBloqueo
) {}