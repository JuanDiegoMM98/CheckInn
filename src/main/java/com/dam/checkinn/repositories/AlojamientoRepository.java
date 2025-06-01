package com.dam.checkinn.repositories;

import com.dam.checkinn.models.AlojamientoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<AlojamientoModel, Integer> {
    boolean existsByNombre(String nombre);

    @Query(
            value = """
    SELECT a.* 
    FROM alojamiento a
    WHERE (:provincia IS NULL OR a.provincia = :provincia)
      AND (:valoracionMin IS NULL OR a.valoracion_media >= :valoracionMin)
      AND (:precioMin IS NULL OR a.precio_noche >= :precioMin)
      AND (:personas IS NULL OR a.capacidad >= :personas)
      AND (
        a.inicio_bloqueo IS NULL 
        OR :fechaSalida <= a.inicio_bloqueo 
        OR :fechaEntrada >= a.fin_bloqueo
      )
      AND NOT EXISTS (
        SELECT 1 FROM reserva r
        WHERE r.alojamiento_id = a.id
          AND r.fechaInicio < :fechaSalida
          AND r.fechaFin > :fechaEntrada
      )
      AND (
        :#{#servicios == null or #servicios.isEmpty()} = true OR 
        (
          SELECT COUNT(DISTINCT s.servicios)
          FROM alojamiento_servicios s
          WHERE s.alojamiento_id = a.id
            AND s.servicios IN (:servicios)
        ) = :#{#servicios.size()}
      )
    """,
            nativeQuery = true
    )
    List<AlojamientoModel> findAllByFiltro(String provincia, Double valoracionMin, Double precioMin, List<String> servicios, LocalDate fechaEntrada, LocalDate fechaSalida, Integer personas);
}
