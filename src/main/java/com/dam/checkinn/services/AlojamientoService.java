package com.dam.checkinn.services;

import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
import com.dam.checkinn.exceptions.AltaAlojamientoException;
import com.dam.checkinn.exceptions.ServerException;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.AlojamientoPatchDTO;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.repositories.AlojamientoRepository;
import com.dam.checkinn.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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

    public AlojamientoModel createAlojamiento(String dni, AlojamientoModel alojamientoModel) throws Exception {
        if (alojamientoRepository.existsByNombre(alojamientoModel.getNombre())) {
            throw new AltaAlojamientoException();
        }
        try {
            Optional<UsuarioModel> usuario = usuarioRepository.findByDniIgnoreCase(dni);
            if (usuario.isEmpty()) {
                throw new AltaAlojamientoException();
            }
            UsuarioModel usuarioDueño = usuario.get();
            if (usuarioDueño.getRol() != UsuarioModel.Rol.PRO) {
                throw new AltaAlojamientoException();
            }
            alojamientoModel.setReservas(new ArrayList<>());
            alojamientoModel.setUsuarioAlojamiento(usuarioDueño);
            return alojamientoRepository.save(alojamientoModel);
        } catch (Exception e) {
            throw new ServerException();
        }
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


    public AlojamientoModel getAlojamientoById(int id) throws Exception {
        Optional<AlojamientoModel> optionalAlojamiento = alojamientoRepository.findById(id);

        if (optionalAlojamiento.isEmpty()) {
            throw new AlojamientoNotFoundException();
        }
        return optionalAlojamiento.get();
    }

    public void deleteAlojamiento(int id) throws Exception {
        if (!alojamientoRepository.existsById(id)) {
            throw new AlojamientoNotFoundException();
        }
        alojamientoRepository.deleteById(id);
    }

    public AlojamientoModel updateAlojamiento(int id, AlojamientoPatchDTO dto) throws Exception {
        if (!alojamientoRepository.existsById(id)) {
            throw new AlojamientoNotFoundException();
        }

        AlojamientoModel alojamiento = alojamientoRepository.findById(id).get();

        alojamiento.setNombre(dto.nombre());
        alojamiento.setDescripcion(dto.descripcion());
        alojamiento.setProvincia(dto.provincia());
        alojamiento.setDireccion(dto.direccion());
        alojamiento.setPrecioNoche(dto.precioNoche());
        alojamiento.setCapacidad(dto.capacidad());
        alojamiento.setImagen(dto.imagen());
        if (dto.servicios() != null) {
            alojamiento.setServicios(dto.servicios());
        }
        if (dto.inicioBloqueo() != null) {
            alojamiento.setInicioBloqueo(dto.inicioBloqueo());
        }
        if (dto.finBloqueo() != null) {
            alojamiento.setFinBloqueo(dto.finBloqueo());
        }

        return alojamientoRepository.save(alojamiento);
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
            if (!(fin.isBefore(reserva.getFechaInicio()) || inicio.isAfter(reserva.getFechaFin()))) {
                return false;
            }
        }

        return true;
    }
}
