package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
import com.dam.checkinn.exceptions.AltaAlojamientoException;
import com.dam.checkinn.exceptions.ServerException;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.models.dto.alojamientos.AlojamientoDTO;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.UsuarioRepository;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
            throw new AlojamientoNotFoundException();
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

    public void deleteAlojamiento(int id) throws Exception {
        if (!alojamientoRepository.existsById(id)) {
            throw new AlojamientoNotFoundException();
        }
        alojamientoRepository.deleteById(id);
    }

    public AlojamientoDTO updateAlojamiento(int id, AlojamientoDTO dto) throws Exception {
        // comprobamos existencia
        if (!alojamientoRepository.existsById(id)) {
            throw new AlojamientoNotFoundException();
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
        Optional<AlojamientoModel> alojamientoOptional = alojamientoRepository.findById(id);
        if (alojamientoOptional.isEmpty()) {
            throw new AlojamientoNotFoundException();
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
}
