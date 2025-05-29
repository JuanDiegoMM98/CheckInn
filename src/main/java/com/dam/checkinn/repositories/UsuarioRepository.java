package com.dam.checkinn.repositories;

import com.dam.checkinn.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, String> {

    Optional<UsuarioModel> findByDniIgnoreCase(String dni);
}
