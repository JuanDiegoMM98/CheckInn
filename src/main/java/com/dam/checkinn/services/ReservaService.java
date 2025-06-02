package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
import com.dam.checkinn.exceptions.ReservaNoValidaException;
import com.dam.checkinn.exceptions.ReservaNotFoundException;
import com.dam.checkinn.models.*;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.ReservaRepository;
import com.dam.checkinn.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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

    public ReservaModel crearReserva(CrearReservaDTO dto) throws Exception {

        // Comprobamos que el alojamiento existe
        if (!alojamientoRepository.existsById(dto.idAlojamiento())) {
            throw new AlojamientoNotFoundException();
        }

        // Comprobamos que el usuario existe
        if (!usuarioRepository.existsById(dto.dni())) {
            throw new AccesoDenegadoException();
        }

        // Obtenemos alojamiento y usuario
        AlojamientoModel alojamientoModel = alojamientoRepository.findById(dto.idAlojamiento()).get();
        UsuarioModel usuarioModel = usuarioRepository.findByDniIgnoreCase(dto.dni()).get();

        // Validación de solapamiento con fechas de bloqueo
        LocalDate inicioBloqueo = alojamientoModel.getInicioBloqueo();
        LocalDate finBloqueo = alojamientoModel.getFinBloqueo();

        if (inicioBloqueo != null && finBloqueo != null &&
                !dto.fechaFin().isBefore(inicioBloqueo) &&
                !dto.fechaInicio().isAfter(finBloqueo)) {
            throw new ReservaNoValidaException();
        }

        // Validacion de solapamiento con otras reservas
        List<ReservaModel> reservasSolapadas = reservaRepository.findReservasSolapadas(
                dto.idAlojamiento(), dto.fechaInicio(), dto.fechaFin());

        if (!reservasSolapadas.isEmpty()) {
            throw new ReservaNoValidaException();
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

    public ReservaModel actualizarReserva(int id, CrearReservaDTO dto) throws Exception {
        // Comprobar que existe reserva
        if (!reservaRepository.existsById(id)) {
            throw new ReservaNotFoundException();
        }

        // Comprobar que existe alojamiento
        if (!alojamientoRepository.existsById(dto.idAlojamiento())) {
            throw new AlojamientoNotFoundException();
        }

        ReservaModel reservaModel = reservaRepository.findById(id).get();

        // Si tiene motivo de cancelacion, modificamos dicha reserva
        if (dto.motivoCancelacion() != null) {
            reservaModel.setMotivoCancelacion(dto.motivoCancelacion());
        }

        // Si el dueño del alojamiento la cancela, modificamos el valor de "cancelada"
        if (dto.cancelada()) {
            reservaModel.setCancelada(dto.cancelada());
        }

        // Si tiene valoracion, modificamos las valoraciones del alojamiento
        if (dto.valoracion() != 0) {
            AlojamientoModel alojamiento = alojamientoRepository.findById(dto.idAlojamiento()).get();
            int contadorValoraciones = alojamiento.getContadorValoraciones();
            double valoracionMedia = alojamiento.getValoracionMedia();

            double nuevaValoracionMedia = ((valoracionMedia * contadorValoraciones) + dto.valoracion()) / (contadorValoraciones + 1);
            alojamiento.setValoracionMedia(nuevaValoracionMedia);
            alojamiento.setContadorValoraciones(contadorValoraciones + 1);
            alojamientoRepository.save(alojamiento);
        }
        return reservaRepository.save(reservaModel);
    }

    public MisReservasDTO reservaRapida(FiltroDTO dto) throws Exception {
        // Comprobamos datos necesarios
        if (dto.dni() == "" || dto.fechaInicio() == null || dto.fechaFin() == null) {
            throw new ReservaNoValidaException();
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
                return new MisReservasDTO(reservaCreada.getId(), reservaCreada.getPrecio(), reservaCreada.isCancelada(), reservaCreada.getFechaInicio(), reservaCreada.getFechaFin(),reservaCreada.getMotivoCancelacion(), alojamientoAleatorio);
            } else {
                throw new AlojamientoNotFoundException();
            }
        } else {
            throw new AlojamientoNotFoundException();
        }
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
