package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.*;
import com.dam.checkinn.models.CredencialesDTO;
import com.dam.checkinn.models.ReservaModel;
import com.dam.checkinn.models.UsuarioDTO;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${api.basepath}")
public class UsuarioController {

    /* DEPENDENCIAS ***************************************************************************************************/

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /* MÉTODOS ********************************************************************************************************/

    @PostMapping("/login")
    public ResponseEntity<UsuarioModel> login(
            @RequestBody CredencialesDTO credencialesDTO) {
        try {
            return ResponseEntity.ok(usuarioService.login(credencialesDTO));
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(401).build();
        } catch (Exception a) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioModel> createUser(
            @RequestBody UsuarioModel usuario) {

        try {
            UsuarioModel nuevoUsuario = usuarioService.createUser(usuario);
            URI location = URI.create("/usuarios/" + nuevoUsuario.getDni());
            return ResponseEntity
                    .created(location) // Esto automáticamente pone el código 201 Created y la cabecera Location
                    .body(nuevoUsuario);
        } catch (AltaUsuarioException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/usuarios/{dni}")
    public ResponseEntity<UsuarioModel> getUsuario(@PathVariable String dni) {
        try {
            UsuarioModel usuario = usuarioService.getUserById(dni);
            return ResponseEntity.ok(usuario);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/usuarios/{dni}")
    public ResponseEntity<UsuarioModel> deleteUser(@PathVariable String dni) {
        try {
            usuarioService.deleteUser(dni);
            return ResponseEntity.ok().build();
        } catch (BorradoUsuarioException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/usuarios/{dni}")
    public ResponseEntity<UsuarioModel> updateUser(
            @PathVariable String dni,
            @RequestBody UsuarioDTO dto) {

        try {
            UsuarioModel usuarioActualizado = usuarioService.updateUser(dni, dto);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuarios/{dni}/reservas")
    public ResponseEntity<List<ReservaModel>> getReservasByDniUsuario(
            @PathVariable String dni
    ) {
        try {
            List<ReservaModel> allReservasByDniUsuario = usuarioService.getAllReservasByDniUsuario(dni);
            return ResponseEntity.ok(allReservasByDniUsuario);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
