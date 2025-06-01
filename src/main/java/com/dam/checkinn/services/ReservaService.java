package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
import com.dam.checkinn.exceptions.ReservaNoValidaException;
import com.dam.checkinn.exceptions.ReservaNotFoundException;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.CrearReservaDTO;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.ReservaRepository;
import com.dam.checkinn.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
                dias*precioNoche,
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
}
