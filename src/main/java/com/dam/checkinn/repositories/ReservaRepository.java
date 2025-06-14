package com.dam.checkinn.repositories;

import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.ReservaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaModel, Integer> {
    List<ReservaModel> findAllByAlojamiento_Id(int idAlojamiento);

    @Query("SELECT r FROM ReservaModel r WHERE r.alojamiento.id = :idAlojamiento AND r.cancelada = false " +
            "AND r.fechaFin >= :nuevaInicio AND r.fechaInicio <= :nuevaFin")
    List<ReservaModel> findReservasSolapadas(
            @Param("idAlojamiento") int idAlojamiento,
            @Param("nuevaInicio") LocalDate nuevaInicio,
            @Param("nuevaFin") LocalDate nuevaFin
    );

    List<ReservaModel> findAllByUsuarioReserva_Id(int usuarioReservaId);

    List<ReservaModel> getById(int id);

    @Query("SELECT r.alojamiento.id FROM ReservaModel r WHERE r.id = :idReserva")
    int findAlojamientoByIdReserva(@Param("idReserva") int idReserva);
}
