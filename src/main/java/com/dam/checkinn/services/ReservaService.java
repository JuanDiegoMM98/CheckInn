package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
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
        if (!reservaRepository.existsById(id)) {
            throw new ReservaNotFoundException();
        }

        ReservaModel reservaModel = reservaRepository.findById(id).get();

        if (dto.motivoCancelacion() != null) {
            reservaModel.setMotivoCancelacion(dto.motivoCancelacion());
        }

        if (dto.cancelada()) {
            reservaModel.setCancelada(dto.cancelada());
        }
        return reservaRepository.save(reservaModel);
    }
}
