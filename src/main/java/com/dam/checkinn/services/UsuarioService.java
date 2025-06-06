package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.*;
import com.dam.checkinn.models.*;
import com.dam.checkinn.models.dto.alojamientos.AlojamientoDTO;
import com.dam.checkinn.models.dto.usuarios.CredencialesLoginDTO;
import com.dam.checkinn.models.dto.reservas.MisReservasDTO;
import com.dam.checkinn.models.dto.usuarios.UsuarioDTO;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.UsuarioRepository;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "dni")
@Service
public class UsuarioService {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;
    private final AlojamientoRepository alojamientoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, AlojamientoRepository alojamientoRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.alojamientoRepository = alojamientoRepository;
    }

    /* MÉTODOS ********************************************************************************************************/

    // LOGIN
    public UsuarioModel login(CredencialesLoginDTO credencialesLoginDTO) throws Exception {
        String dni = credencialesLoginDTO.dni();
        String contraseña = credencialesLoginDTO.contraseña();

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

    // CREAR
    @Transactional
    public UsuarioModel createUser(UsuarioModel usuario) throws Exception {

        if (usuarioRepository.existsByDni(usuario.getDni()) || usuarioRepository.existsByCorreoLikeIgnoreCase(usuario.getCorreo())) {
            throw new DatosNoValidosException();
        }

        if (LocalDate.now().getYear() - usuario.getFechaNacimiento().getYear() < 18) {
            throw new DatosNoValidosException();
        }

        try {
            String contraseña = usuario.getContraseña();
            usuario.setContraseña(passwordEncoder.encode(contraseña));
            usuario.setAlojamientos(new ArrayList<>());
            usuario.setReservas(new ArrayList<>());
            if (usuario.getTelefono().equals("")) {
                usuario.setTelefono(null);
            }
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new DatosNoValidosException();
        }
    }


    // GET USUARIO INDIVIDUAL (AREA PERSONAL)
    public UsuarioModel getUserById(int id) throws Exception {
        // Seguridad
        filtroSeguridad(id);

        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }

        return usuarioRepository.findById(id).get();
    }

    // BORRADO
    @Transactional
    public void deleteUser(int id) throws Exception {
        filtroSeguridad(id);
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }
        usuarioRepository.deleteById(id);
    }

    // ACTUALIZACION
    @Transactional
    public UsuarioModel updateUser(int id, UsuarioDTO dto) throws Exception {
        filtroSeguridad(id);

        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }

        UsuarioModel usuarioModel = usuarioRepository.findById(id).get();

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
                throw new DatosNoValidosException();
            }
        }
        usuarioModel.setFechaNacimiento(dto.fechaNacimiento());
        usuarioModel.setRol(UsuarioModel.Rol.valueOf(dto.rol()));
        usuarioModel.setTelefono(dto.telefono());
        usuarioModel.setGenero(UsuarioModel.Genero.valueOf(dto.genero()));

        return usuarioRepository.save(usuarioModel);
    }

    public List<MisReservasDTO> getAllReservasByIdUsuario(int id) throws Exception {
        filtroSeguridad(id);

        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }

        UsuarioModel usuarioModel = usuarioRepository.findById(id).get();

        List<ReservaModel> reservas = usuarioModel.getReservas();

        List<MisReservasDTO> misReservasDTO = new ArrayList<>();
        reservas.forEach(r -> {
            MisReservasDTO reservaDTO = new MisReservasDTO(r.getId(), r.getPrecio(), r.isCancelada(), r.isValorada(),
                    r.getFechaInicio(), r.getFechaFin(), r.getMotivoCancelacion(), r.getAlojamiento().getId(), r.getAlojamiento().getImagen(),
                    r.getAlojamiento().getNombre(), r.getAlojamiento().getDireccion(), r.getAlojamiento().getCapacidad());
            misReservasDTO.add(reservaDTO);
        });
        return misReservasDTO;
    }

    @Transactional
    public AlojamientoModel guardarAlojamientoDeUsuario(int id, AlojamientoDTO alojamientoDTO) throws Exception {
        filtroSeguridad(id);

        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }

        // Comprobamos que no exista alojamiento con el mismo nombre
        if (alojamientoRepository.existsByNombre(alojamientoDTO.nombre())) {
            throw new DatosNoValidosException();
        }
        // Creamos alojamiento
        AlojamientoModel alojamientoModel = new AlojamientoModel(
                alojamientoDTO.nombre(),
                alojamientoDTO.descripcion(),
                alojamientoDTO.provincia(),
                alojamientoDTO.direccion(),
                alojamientoDTO.precioNoche(),
                alojamientoDTO.capacidad(),
                0.0,
                alojamientoDTO.imagen(),
                alojamientoDTO.servicios(),
                alojamientoDTO.inicioBloqueo(),
                alojamientoDTO.finBloqueo()
        );

        // Buscamos al usuario
        UsuarioModel usuarioModel = usuarioRepository.findById(id).get();

        // Si no es pro, no le dejamos
        if (usuarioModel.getRol() != UsuarioModel.Rol.PRO) {
            throw new DatosNoValidosException();
        }

        // Le añadimos su alojamiento
        usuarioModel.getAlojamientos().add(alojamientoModel);

        // Guardamos el usuario
        UsuarioModel usuarioActualizado = usuarioRepository.save(usuarioModel);
        AlojamientoModel alojamientoCreado = usuarioActualizado.getAlojamientos().stream()
                .filter(a -> a
                        .getNombre().equals(alojamientoModel.getNombre()))
                .findFirst().get();

        // Devolvemos el alojamiento creado
        return alojamientoCreado;
    }

    /* SEGURIDAD ******************************************************************************************************/

    private void filtroSeguridad(int id) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UsuarioModel) {
            UsuarioModel usuario = (UsuarioModel) auth.getPrincipal();
            if (usuario.getId() != id) {
                logout();
            }

        }
    }

    private void logout() throws Exception {
        // Obtener el request actual
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        HttpServletResponse response = attr.getResponse();
        // Invalidar la sesión
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Limpiar contexto de seguridad
        SecurityContextHolder.clearContext();
        throw new AccesoDenegadoException();
//        response.sendRedirect("/login?logout");
    }
}
