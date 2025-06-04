package com.dam.checkinn.models.dto.alojamientos;

import com.dam.checkinn.models.AlojamientoModel;

import java.util.List;

public record MisAlojamientosDTO(
        List<AlojamientoModel> alojamientos
) {
}
