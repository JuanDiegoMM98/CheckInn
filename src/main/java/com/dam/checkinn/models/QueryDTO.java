package com.dam.checkinn.models;

public record QueryDTO(
        String provincia,
        Double valoracionMinima,
        Double precioMaximo,
        Integer personasMaximas
) {

}
