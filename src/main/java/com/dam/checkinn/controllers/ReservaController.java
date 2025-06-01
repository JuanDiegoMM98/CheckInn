package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
import com.dam.checkinn.exceptions.ReservaNotFoundException;
import com.dam.checkinn.models.CrearReservaDTO;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.services.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.basepath}/reservas")
public class ReservaController {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /* MÉTODOS ********************************************************************************************************/

    @PostMapping
    public ResponseEntity<ReservaModel> createReserva(@RequestBody CrearReservaDTO dto) {
        try {
            ReservaModel nuevaReserva = reservaService.crearReserva(dto);
            URI location = URI.create("/reservas/" + nuevaReserva.getId());
            return ResponseEntity
                    .created(location) // Esto automáticamente pone el código 201 Created y la cabecera Location
                    .body(nuevaReserva);
        } catch (AlojamientoNotFoundException |AccesoDenegadoException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReservaModel> updateReserva(
            @PathVariable int id,
            @RequestBody CrearReservaDTO dto) {
        try {
            ReservaModel reservaActualizada = reservaService.actualizarReserva(id, dto);
            return ResponseEntity.ok(reservaActualizada);
        } catch (ReservaNotFoundException | AlojamientoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
