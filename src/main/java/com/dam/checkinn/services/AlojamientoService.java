package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.RecursoNotFoundException;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.models.dto.alojamientos.AlojamientoDTO;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlojamientoService {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final AlojamientoRepository alojamientoRepository;

    private final UsuarioRepository usuarioRepository;

    public AlojamientoService(AlojamientoRepository alojamientoRepository, UsuarioRepository usuarioRepository) {
        this.alojamientoRepository = alojamientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /* MÉTODOS ********************************************************************************************************/

    @Transactional
    public void deleteAlojamiento(int id) throws Exception {
        filtroSeguridad(id);

        if (!alojamientoRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }
        alojamientoRepository.deleteById(id);
    }


    public List<AlojamientoModel> buscarDisponiblesConFiltro(String provincia, Double valoracionMinima, Double precioMaximo,
                                                             List<AlojamientoModel.Servicio> servicios, LocalDate fechaInicio, LocalDate fechaFin, Integer personasMaximas
    ) {
        List<AlojamientoModel> candidatos = alojamientoRepository.findByFiltrosBasicos(provincia, valoracionMinima, precioMaximo,
                personasMaximas
        );

        return candidatos.stream()
                .filter(a -> tieneServicios(a, servicios))
                .filter(a -> estaDisponiblePorFechas(a, fechaInicio, fechaFin))
                .collect(Collectors.toList());
    }


    public AlojamientoDTO getAlojamientoById(int id) throws Exception {
        // Comprobamos que existe
        Optional<AlojamientoModel> optionalAlojamiento = alojamientoRepository.findById(id);
        if (optionalAlojamiento.isEmpty()) {
            throw new RecursoNotFoundException();
        }
        AlojamientoModel alojamientoModel = optionalAlojamiento.get();

        // Recuperamos sus reservas
        List<ReservaModel> reservasAlojamiento = alojamientoModel.getReservas();

        // Creamos DTO y lo devolvemos
        return new AlojamientoDTO(alojamientoModel.getNombre(), alojamientoModel.getDescripcion(),
                alojamientoModel.getProvincia(), alojamientoModel.getDireccion(), alojamientoModel.getPrecioNoche(),
                alojamientoModel.getValoracionMedia(), alojamientoModel.getCapacidad(), alojamientoModel.getImagen(),
                alojamientoModel.getServicios(), alojamientoModel.getInicioBloqueo(),
                alojamientoModel.getFinBloqueo()
        );
    }

    @Transactional
    public AlojamientoDTO updateAlojamiento(int id, AlojamientoDTO dto) throws Exception {
        filtroSeguridad(id);
        // comprobamos existencia
        if (!alojamientoRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }

        // Buscamos el alojamiento y modificamos sus datos con los que vienen
        AlojamientoModel alojamiento = alojamientoRepository.findById(id).get();

        alojamiento.setNombre(dto.nombre());
        alojamiento.setDescripcion(dto.descripcion());
        alojamiento.setProvincia(dto.provincia());
        alojamiento.setDireccion(dto.direccion());
        alojamiento.setPrecioNoche(dto.precioNoche());
        alojamiento.setCapacidad(dto.capacidad());
        alojamiento.setImagen(dto.imagen());

        // Verificamos lo que nos envía el frontal
        if (dto.servicios() != null) {
            alojamiento.setServicios(dto.servicios());
        }
        if (dto.inicioBloqueo() != null) {
            alojamiento.setInicioBloqueo(dto.inicioBloqueo());
        }
        if (dto.finBloqueo() != null) {
            alojamiento.setFinBloqueo(dto.finBloqueo());
        }

        // Obtenemos la modificación
        AlojamientoModel alojamientoModificadoBD = alojamientoRepository.save(alojamiento);

        // Devolvemos el DTO
        return new AlojamientoDTO(alojamientoModificadoBD.getNombre(), alojamientoModificadoBD.getDescripcion(),
                alojamientoModificadoBD.getProvincia(), alojamientoModificadoBD.getDireccion(), alojamientoModificadoBD.getPrecioNoche(),
                alojamientoModificadoBD.getValoracionMedia(), alojamientoModificadoBD.getCapacidad(), alojamientoModificadoBD.getImagen(),
                alojamientoModificadoBD.getServicios(), alojamientoModificadoBD.getInicioBloqueo(), alojamientoModificadoBD.getFinBloqueo()
        );
    }

    public List<ReservaModel> getAllReservasByIdAlojamiento(int id) throws Exception {
        filtroSeguridad(id);
        Optional<AlojamientoModel> alojamientoOptional = alojamientoRepository.findById(id);
        if (alojamientoOptional.isEmpty()) {
            throw new RecursoNotFoundException();
        }
        AlojamientoModel alojamientoModel = alojamientoOptional.get();
        return alojamientoModel.getReservas();
    }

    /* AUXILIARES *****************************************************************************************************/

    private boolean tieneServicios(AlojamientoModel alojamiento, List<AlojamientoModel.Servicio> serviciosRequeridos) {
        if (serviciosRequeridos == null || serviciosRequeridos.isEmpty()) return true;
        return alojamiento.getServicios() != null && alojamiento.getServicios().containsAll(serviciosRequeridos);
    }

    private boolean estaDisponiblePorFechas(AlojamientoModel alojamiento, LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) return true;

        // Verifica si está bloqueado
        if (alojamiento.getInicioBloqueo() != null && alojamiento.getFinBloqueo() != null) {
            if (!(fin.isBefore(alojamiento.getInicioBloqueo()) || inicio.isAfter(alojamiento.getFinBloqueo()))) {
                return false;
            }
        }

        // Verifica colisiones con reservas existentes
        for (ReservaModel reserva : alojamiento.getReservas()) {
            if (reserva.isCancelada()) continue;
            if (!(fin.isBefore(reserva.getFechaInicio()) || inicio.isAfter(reserva.getFechaFin()))) {
                return false;
            }
        }

        return true;
    }

    /* SEGURIDAD ******************************************************************************************************/

    private void filtroSeguridad(int id) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UsuarioModel usuario) {
            // Conseguimos ID de usuario
            int idUsuario = usuario.getId();

            // Conseguimos sus alojamientos
            List<AlojamientoModel> alojamientosUsuario = alojamientoRepository.findAllByUsuarioAlojamiento_Id(idUsuario);

            // Vemos si alguno de sus IDs coincide con el id de la url
            boolean encontrado = alojamientosUsuario.stream()
                    .anyMatch(a -> a.getId() == id);

            if (!encontrado) {
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
