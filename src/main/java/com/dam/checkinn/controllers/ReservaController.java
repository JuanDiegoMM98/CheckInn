package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.*;
import com.dam.checkinn.models.dto.reservas.CrearActualizarReservaDTOFront;
import com.dam.checkinn.models.dto.alojamientos.FiltroDTO;
import com.dam.checkinn.models.dto.reservas.MisReservasDTO;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.services.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${api.basepath}/reservas")
public class ReservaController {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /* MÉTODOS ********************************************************************************************************/

    @GetMapping("/{id}")
    public ResponseEntity<MisReservasDTO> getReservaIndividual(@PathVariable int id) {
        try {
            MisReservasDTO reservaDTO = reservaService.getReservaIndividual(id);
            return ResponseEntity.ok(reservaDTO);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<ReservaModel> createReserva(@RequestBody CrearActualizarReservaDTOFront dto) {
        try {
            ReservaModel nuevaReserva = reservaService.crearReserva(dto);
            URI location = URI.create("/reservas/" + nuevaReserva.getId());
            return ResponseEntity.created(location).body(nuevaReserva);
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (DatosNoValidosException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservaModel> updateReserva(
            @PathVariable int id,
            @RequestBody CrearActualizarReservaDTOFront dto) {
        try {
            ReservaModel reservaActualizada = reservaService.actualizarReserva(id, dto);
            return ResponseEntity.ok(reservaActualizada);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/reservaRapida")
    public ResponseEntity<MisReservasDTO> reservaRapida(
            @RequestBody FiltroDTO dto) {
        try {
            MisReservasDTO reservaRapida = reservaService.reservaRapida(dto);
            return ResponseEntity.ok(reservaRapida);
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (DatosNoValidosException e) {
             return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
