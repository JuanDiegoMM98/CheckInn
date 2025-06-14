package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.*;
import com.dam.checkinn.models.*;
import com.dam.checkinn.models.dto.reservas.CrearActualizarReservaDTOFront;
import com.dam.checkinn.models.dto.alojamientos.FiltroDTO;
import com.dam.checkinn.models.dto.reservas.MisReservasDTO;
import com.dam.checkinn.models.dto.reservas.QueryDTO;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.ReservaRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReservaService {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final UsuarioRepository usuarioRepository;

    private final ReservaRepository reservaRepository;

    private final AlojamientoRepository alojamientoRepository;

    public ReservaService(UsuarioRepository usuarioRepository, ReservaRepository reservaRepository, AlojamientoRepository alojamientoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    /* MÉTODOS ********************************************************************************************************/

    public MisReservasDTO getReservaIndividual(int id) throws Exception {
        filtroSeguridad(id);

        // Comprobar que existe reserva
        if (!reservaRepository.existsById(id)) {
            throw new RecursoNotFoundException();
        }

        // La obtenemos de BD
        ReservaModel reservaModel = reservaRepository.findById(id).get();

        // Mapeamos la respuesta
        MisReservasDTO dto = new MisReservasDTO(
                reservaModel.getId(), reservaModel.getPrecio(), reservaModel.isCancelada(), reservaModel.isValorada(),
                reservaModel.getFechaInicio(), reservaModel.getFechaFin(), reservaModel.getMotivoCancelacion(),
                reservaModel.getAlojamiento().getId(), reservaModel.getAlojamiento().getImagen(),
                reservaModel.getAlojamiento().getNombre(), reservaModel.getAlojamiento().getDireccion(), reservaModel.getAlojamiento().getCapacidad()
        );

        return dto;
    }

    @Transactional
    public ReservaModel crearReserva(CrearActualizarReservaDTOFront dto) throws Exception {

        // Comprobamos que el alojamiento existe
        if (!alojamientoRepository.existsById(dto.idAlojamiento())) {
            throw new RecursoNotFoundException();
        }

        // Comprobamos que el usuario existe
        if (!usuarioRepository.existsById(dto.idUsuario())) {
            throw new RecursoNotFoundException();
        }

        // Obtenemos alojamiento y usuario
        AlojamientoModel alojamientoModel = alojamientoRepository.findById(dto.idAlojamiento()).get();
        UsuarioModel usuarioModel = usuarioRepository.findById(dto.idUsuario()).get();

        // Validación de solapamiento con fechas de bloqueo
        LocalDate inicioBloqueo = alojamientoModel.getInicioBloqueo();
        LocalDate finBloqueo = alojamientoModel.getFinBloqueo();

        if (inicioBloqueo != null && finBloqueo != null &&
                !dto.fechaFin().isBefore(inicioBloqueo) &&
                !dto.fechaInicio().isAfter(finBloqueo)) {
            throw new DatosNoValidosException();
        }

        // Validacion de solapamiento con otras reservas
        List<ReservaModel> reservasSolapadas = reservaRepository.findReservasSolapadas(
                dto.idAlojamiento(), dto.fechaInicio(), dto.fechaFin());

        if (!reservasSolapadas.isEmpty()) {
            throw new DatosNoValidosException();
        }

        // Calculamos precio
        double precioNoche = alojamientoModel.getPrecioNoche();
        int dias = (int) ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin());

        // Creamos el objeto del modelo
        ReservaModel reservaModel = new ReservaModel(
                dias * precioNoche,
                dto.fechaInicio(),
                dto.fechaFin(),
                usuarioModel,
                alojamientoModel,
                null // Al crear la reserva, no hay motivos de cancelación aunque aparezca en DTO
        );
        return reservaRepository.save(reservaModel);
    }

    @Transactional
    public ReservaModel actualizarReserva(int idReserva, CrearActualizarReservaDTOFront dto) throws Exception {
        filtroSeguridad(idReserva);

        ReservaModel reservaModel = reservaRepository.findById(idReserva).get();

        // Si tiene motivo de cancelacion, modificamos dicha reserva
        if (dto.motivoCancelacion() != null) {
            reservaModel.setMotivoCancelacion(dto.motivoCancelacion());
        }

        // Si el dueño del alojamiento la cancela, modificamos el valor de "cancelada"
        if (dto.cancelada()) {
            reservaModel.setCancelada(true);
        }

        // Si tiene valoracion, modificamos las valoraciones del alojamiento y la ponemos como valorada
        if (dto.valoracion() != 0) {

            int idAlojamiento = reservaRepository.findAlojamientoByIdReserva(idReserva);
            AlojamientoModel alojamiento = alojamientoRepository.findById(idAlojamiento).get();
            int contadorValoraciones = alojamiento.getContadorValoraciones();
            double valoracionMedia = alojamiento.getValoracionMedia();

            double nuevaValoracionMedia = ((valoracionMedia * contadorValoraciones) + dto.valoracion()) / (contadorValoraciones + 1);
            alojamiento.setValoracionMedia(nuevaValoracionMedia);
            alojamiento.setContadorValoraciones(contadorValoraciones + 1);
            reservaModel.setValorada(true);
            alojamientoRepository.save(alojamiento);
        }
        return reservaRepository.save(reservaModel);
    }

    @Transactional
    public MisReservasDTO reservaRapida(FiltroDTO dto) throws Exception {
        // Comprobamos datos necesarios
        if (dto.dni() == "" || dto.fechaInicio() == null || dto.fechaFin() == null) {
            throw new DatosNoValidosException();
        }

        // Parseamos valores a los que va a recibir el metodo
        String provincia = null;
        Double valoracionMinima = null;
        Double precioMaximo = null;
        Integer personasMaximas = null;

        if (!dto.provincia().equals("")) {
            provincia = dto.provincia();
        }
        if (dto.valoracionMinima() != null) {
            valoracionMinima = dto.valoracionMinima();
        }
        if (dto.precioMaximo() != null) {
            precioMaximo = dto.precioMaximo();
        }
        if (dto.personasMaximas() != null) {
            personasMaximas = dto.personasMaximas();
        }

        QueryDTO query = new QueryDTO(provincia, valoracionMinima, precioMaximo, personasMaximas);

        // Tras el primer filtrado
        List<AlojamientoModel> alojamientosPosibles = alojamientoRepository.findByFiltrosBasicos(
                query.provincia(), query.valoracionMinima(), query.precioMaximo(), query.personasMaximas()
        );

        if (!alojamientosPosibles.isEmpty()) {
            List<AlojamientoModel> candidatos = alojamientosPosibles.stream()
                    .filter(a -> tieneServicios(a, dto.servicios()))
                    .filter(a -> estaDisponiblePorFechas(a, dto.fechaInicio(), dto.fechaFin()))
                    .toList();
            if (!candidatos.isEmpty()) {
                int indiceAleatorio = ThreadLocalRandom.current().nextInt(alojamientosPosibles.size());
                AlojamientoModel alojamientoAleatorio = candidatos.get(indiceAleatorio);

                // Creamos el objeto reserva
                ReservaModel reserva = new ReservaModel(
                        alojamientoAleatorio.getPrecioNoche() * (int) ChronoUnit.DAYS.between(dto.fechaInicio(), dto.fechaFin()),
                        dto.fechaInicio(),
                        dto.fechaFin(),
                        usuarioRepository.findByDniIgnoreCase(dto.dni()).get(),
                        alojamientoAleatorio,
                        null
                );
                ReservaModel reservaCreada = reservaRepository.save(reserva);
                return new MisReservasDTO(reservaCreada.getId(), reservaCreada.getPrecio(), reservaCreada.isCancelada(),
                        false, reservaCreada.getFechaInicio(), reservaCreada.getFechaFin(), reservaCreada.getMotivoCancelacion(),
                        reserva.getAlojamiento().getId(), reserva.getAlojamiento().getImagen(), reserva.getAlojamiento().getNombre(),
                        reserva.getAlojamiento().getDireccion(), reserva.getAlojamiento().getCapacidad());
            } else {
                throw new RecursoNotFoundException();
            }
        } else {
            throw new RecursoNotFoundException();
        }
    }

    /* SEGURIDAD ******************************************************************************************************/

    private void filtroSeguridad(int idReserva) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof UsuarioModel usuario) {
            // Usuario
            int idUsuario = usuario.getId();

            // 1. Comprobamos si el usuario tiene la reserva directamente
            List<ReservaModel> reservasUsuario = reservaRepository.findAllByUsuarioReserva_Id(idUsuario);

            boolean enReservasDelUsuario = reservasUsuario != null &&
                    reservasUsuario.stream().anyMatch(r -> r.getId() == idReserva);

            if (enReservasDelUsuario) {
                return; // Seguridad OK
            }

            // 2. Comprobamos si alguno de sus alojamientos contiene la reserva
            List<AlojamientoModel> alojamientosUsuario = alojamientoRepository.findAllByUsuarioAlojamiento_Id(idUsuario);
            boolean enAlojamientosDelUsuario = alojamientosUsuario != null &&
                    alojamientosUsuario.stream()
                            .flatMap(a -> a.getReservas().stream())
                            .anyMatch(r -> r.getId() == idReserva);

            if (!enAlojamientosDelUsuario) {
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
}
