package com.dam.checkinn.repositories;

import com.dam.checkinn.models.UsuarioModel;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, String> {

    Optional<UsuarioModel> findUsuarioModelById(int id);

    boolean existsByDni(String dni);

    void deleteById(int id);

    Optional<UsuarioModel> findByDniIgnoreCase(String dni);

    Optional<UsuarioModel> findById(int id);

    List<UsuarioModel> getById(int id);

    boolean existsById(int id);

    boolean existsByCorreoLikeIgnoreCase(String correo);

    UsuarioModel getUsuarioModelByDni(String dni);
}
