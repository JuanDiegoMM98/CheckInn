package com.dam.checkinn.models.dto.alojamientos;

import java.time.LocalDate;

public record FacturacionDTO(int idAlojamiento, LocalDate fechaInicio, LocalDate fechaFin, double totalFacturado) {
}
