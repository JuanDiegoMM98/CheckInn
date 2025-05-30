package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AlojamientoNotFoundException;
import com.dam.checkinn.exceptions.AltaAlojamientoException;
import com.dam.checkinn.exceptions.BorradoUsuarioException;
import com.dam.checkinn.models.AlojamientoModel;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.services.AlojamientoService;
import com.dam.checkinn.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.basepath}/alojamientos")
public class AlojamientoController {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final AlojamientoService alojamientoService;
    private final UsuarioService usuarioService;

    public AlojamientoController(AlojamientoService alojamientoService, UsuarioService usuarioService) {
        this.alojamientoService = alojamientoService;
        this.usuarioService = usuarioService;
    }

    /* MÉTODOS ********************************************************************************************************/

    @PostMapping
    public ResponseEntity<AlojamientoModel> createAlojamiento(
            @RequestParam String dni,
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
    public ResponseEntity<List<AlojamientoModel>> getAllAlojamientos() {
        try {
            List<AlojamientoModel> allAlojamientos = alojamientoService.getAllAlojamientos();
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

    @PatchMapping()
    public ResponseEntity<AlojamientoModel> updateAlojamiento(
            @RequestParam int id,
            @RequestBody AlojamientoModel alojamiento) {

        try {
            AlojamientoModel alojamientoActualizado = alojamientoService.updateAlojamiento(id, alojamiento);
            return ResponseEntity.ok(alojamientoActualizado);
        } catch (AlojamientoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
