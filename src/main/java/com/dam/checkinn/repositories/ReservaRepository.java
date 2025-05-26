package com.dam.checkinn.repositories;

import com.dam.checkinn.models.ReservaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaModel, Integer> {
}
