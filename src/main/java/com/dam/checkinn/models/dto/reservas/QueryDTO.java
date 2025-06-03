package com.dam.checkinn.models.dto.reservas;

public record QueryDTO(
        String provincia,
        Double valoracionMinima,
        Double precioMaximo,
        Integer personasMaximas
) {

}
