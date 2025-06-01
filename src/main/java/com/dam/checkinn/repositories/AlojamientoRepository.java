package com.dam.checkinn.repositories;

import com.dam.checkinn.models.AlojamientoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<AlojamientoModel, Integer> {
    boolean existsByNombre(String nombre);

    @Query("SELECT a FROM AlojamientoModel a WHERE " +
            "(:provincia IS NULL OR a.provincia = :provincia) AND " +
            "(:valoracionMinima IS NULL OR a.valoracionMedia >= :valoracionMinima) AND " +
            "(:precioMaximo IS NULL OR a.precioNoche <= :precioMaximo) AND " +
            "(:personasMaximas IS NULL OR a.capacidad >= :personasMaximas)")
    List<AlojamientoModel> findByFiltrosBasicos(
            @Param("provincia") String provincia,
            @Param("valoracionMinima") Double valoracionMinima,
            @Param("precioMaximo") Double precioMaximo,
            @Param("personasMaximas") Integer personasMaximas
    );
}
