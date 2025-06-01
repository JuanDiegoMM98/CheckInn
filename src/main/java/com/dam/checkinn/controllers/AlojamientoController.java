package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
import com.dam.checkinn.exceptions.AltaAlojamientoException;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.AlojamientoPatchDTO;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioModel;
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

    @PostMapping("/{dni}")
    public ResponseEntity<AlojamientoModel> createAlojamiento(
            @PathVariable String dni,
            @RequestBody AlojamientoModel alojamiento) {
        try {
            System.out.println("entra en metodo");
            AlojamientoModel nuevoAlojamiento = alojamientoService.createAlojamiento(dni, alojamiento);
            URI location = URI.create("/alojamientos/" + nuevoAlojamiento.getId());
            return ResponseEntity
                    .created(location) // Esto automáticamente pone el código 201 Created y la cabecera Location
                    .body(nuevoAlojamiento);
        } catch (AltaAlojamientoException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UsuarioModel> deleteAlojamiento(@PathVariable int id) {
        try {
            alojamientoService.deleteAlojamiento(id);
            return ResponseEntity.ok().build();
        } catch (AlojamientoNotFoundException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<AlojamientoModel>> getAlojamientosByFiltro(
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) Double valoracionMin,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) List<String> servicios,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam(required = false) Integer personas
    ) {
        try {
            List<AlojamientoModel> allAlojamientos = alojamientoService.getAlojamientosByFiltro(provincia,
                    valoracionMin,
                    precioMin,
                    servicios,
                    fechaEntrada,
                    fechaSalida,
                    personas);
            return ResponseEntity.ok(allAlojamientos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlojamientoModel> getAlojamientoById(@PathVariable int id) {
        try {
            AlojamientoModel alojamiento = alojamientoService.getAlojamientoById(id);
            return ResponseEntity.ok(alojamiento);
        } catch (AlojamientoNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlojamientoModel> updateAlojamiento(
            @PathVariable int id,
            @RequestBody AlojamientoPatchDTO alojamiento) {

        try {
            AlojamientoModel alojamientoActualizado = alojamientoService.updateAlojamiento(id, alojamiento);
            return ResponseEntity.ok(alojamientoActualizado);
        } catch (AlojamientoNotFoundException e) {
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
        } catch (AlojamientoNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
