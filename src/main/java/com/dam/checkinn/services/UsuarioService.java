package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AltaUsuarioException;
import com.dam.checkinn.exceptions.BorradoUsuarioException;
import com.dam.checkinn.exceptions.ServerException;
import com.dam.checkinn.models.Credenciales;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UsuarioService {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* MÉTODOS ********************************************************************************************************/

    public UsuarioModel login(Credenciales credenciales) throws Exception {
        String dni = credenciales.dni();
        String contraseña = credenciales.contraseña();

        Optional<UsuarioModel> usuarioConsultado = usuarioRepository.findByDniIgnoreCase(dni);

        if (usuarioConsultado.isEmpty()) {
            throw new AccesoDenegadoException();
        }

        UsuarioModel usuario = usuarioConsultado.get();

        if (!passwordEncoder.matches(contraseña, usuario.getContraseña())) {
            throw new AccesoDenegadoException();
        }

        return usuario;
    }

    public UsuarioModel createUser(UsuarioModel usuario) throws Exception {

        if (usuarioRepository.existsById(usuario.getDni())) {
            throw new AltaUsuarioException();
        }

        if (LocalDate.now().getYear() - usuario.getFechaNacimiento().getYear() < 18 ) {
            throw new AltaUsuarioException();
        }

        //


        try {
            String contraseña = usuario.getContraseña();
            usuario.setContraseña(passwordEncoder.encode(contraseña));
            usuario.setAlojamientos(new ArrayList<>());
            usuario.setReservas(new ArrayList<>());
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new AltaUsuarioException();
        }
    }


    public UsuarioModel getUserById(String dni) throws Exception {
        Optional<UsuarioModel> usuario = usuarioRepository.findByDniIgnoreCase(dni);

        if (usuario.isEmpty()) {
            throw new AccesoDenegadoException();
        }
        return usuario.get();
    }

    public void deleteUser(String dni) throws Exception {

        if (!usuarioRepository.existsById(dni)) {
            throw new BorradoUsuarioException();
        }
        usuarioRepository.deleteById(dni);
    }

    public UsuarioModel updateUser(String dni, UsuarioModel usuario) throws Exception {
        if (!usuarioRepository.existsById(dni) || !dni.equals(usuario.getDni())) {
            throw new AccesoDenegadoException();
        }

        return usuarioRepository.save(usuario);
    }
}
