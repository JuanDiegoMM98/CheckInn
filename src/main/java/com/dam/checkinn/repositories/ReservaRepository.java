package com.dam.checkinn.repositories;

import com.dam.checkinn.models.ReservaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaModel, Integer> {
    List<ReservaModel> findAllByAlojamiento_Id(int idAlojamiento);
}
