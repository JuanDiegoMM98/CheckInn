package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AltaUsuarioException;
import com.dam.checkinn.exceptions.BorradoUsuarioException;
import com.dam.checkinn.models.CredencialesDTO;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioDTO;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UsuarioModel login(CredencialesDTO credencialesDTO) throws Exception {
        String dni = credencialesDTO.dni();
        String contraseña = credencialesDTO.contraseña();

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

        if (LocalDate.now().getYear() - usuario.getFechaNacimiento().getYear() < 18) {
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

    public UsuarioModel updateUser(String dni, UsuarioDTO dto) throws Exception {
        if (!usuarioRepository.existsById(dni)) {
            throw new AccesoDenegadoException();
        }

        UsuarioModel usuarioModel = usuarioRepository.findByDniIgnoreCase(dni).get();

        usuarioModel.setNombre(dto.nombre());
        usuarioModel.setApellido1(dto.apellido1());
        usuarioModel.setApellido2(dto.apellido2());
        usuarioModel.setCorreo(dto.correo());
        usuarioModel.setContraseña(passwordEncoder.encode(dto.contraseña()));
        usuarioModel.setTarjetaBancaria(dto.tarjetaBancaria());
        usuarioModel.setDireccion(dto.direccion());
        if (dto.fechaNacimiento() != null) {
            if (LocalDate.now().getYear() - dto.fechaNacimiento().getYear() >= 18) {
                usuarioModel.setFechaNacimiento(dto.fechaNacimiento());
            } else {
                throw new AltaUsuarioException();
            }
        }
        usuarioModel.setFechaNacimiento(dto.fechaNacimiento());
        usuarioModel.setRol(UsuarioModel.Rol.valueOf(dto.rol()));
        usuarioModel.setTelefono(dto.telefono());
        usuarioModel.setGenero(UsuarioModel.Genero.valueOf(dto.genero()));

        return usuarioRepository.save(usuarioModel);
    }

    public List<ReservaModel> getAllReservasByDniUsuario(String dni) throws Exception {

        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findByDniIgnoreCase(dni);
        if (usuarioOptional.isEmpty()) {
            throw new AccesoDenegadoException();
        }
        return usuarioOptional.get().getReservas();
    }
}
