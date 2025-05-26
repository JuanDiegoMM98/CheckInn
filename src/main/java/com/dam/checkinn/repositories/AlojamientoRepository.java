package com.dam.checkinn.repositories;

import com.dam.checkinn.models.AlojamientoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlojamientoRepository extends JpaRepository<AlojamientoModel, Integer> {
}
