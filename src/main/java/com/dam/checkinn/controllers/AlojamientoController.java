package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.RecursoNotFoundException;
import com.dam.checkinn.exceptions.DatosNoValidosException;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.models.dto.alojamientos.AlojamientoDTO;
import com.dam.checkinn.services.AlojamientoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${api.basepath}/alojamientos")
public class AlojamientoController {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final AlojamientoService alojamientoService;

    public AlojamientoController(AlojamientoService alojamientoService) {
        this.alojamientoService = alojamientoService;
    }

    /* MÉTODOS ********************************************************************************************************/

    @DeleteMapping("{id}")
    public ResponseEntity<UsuarioModel> deleteAlojamiento(@PathVariable int id) {
        try {
            alojamientoService.deleteAlojamiento(id);
            return ResponseEntity.ok().build();
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<AlojamientoModel>> filtrarAlojamientos(
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) Double valoracionMinima,
            @RequestParam(required = false) Double precioMaximo,
            @RequestParam(required = false) List<AlojamientoModel.Servicio> servicios,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Integer personasMaximas
    ) {
        List<AlojamientoModel> alojamientos = alojamientoService.buscarDisponiblesConFiltro(
                provincia,
                valoracionMinima,
                precioMaximo,
                servicios,
                fechaInicio,
                fechaFin,
                personasMaximas
        );

        return ResponseEntity.ok(alojamientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlojamientoDTO> getAlojamientoById(@PathVariable int id) {
        try {
            AlojamientoDTO alojamiento = alojamientoService.getAlojamientoById(id);
            return ResponseEntity.ok(alojamiento);
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlojamientoDTO> updateAlojamiento(
            @PathVariable int id,
            @RequestBody AlojamientoDTO alojamiento) {

        try {
            AlojamientoDTO alojamientoActualizado = alojamientoService.updateAlojamiento(id, alojamiento);
            return ResponseEntity.ok(alojamientoActualizado);
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<ReservaModel>> getReservasByIdAlojamiento(
            @PathVariable int id
    ) {
        try {
            List<ReservaModel> allReservasByIdAlojamiento = alojamientoService.getAllReservasByIdAlojamiento(id);
            return ResponseEntity.ok(allReservasByIdAlojamiento);
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
