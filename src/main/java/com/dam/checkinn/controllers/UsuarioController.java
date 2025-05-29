package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.AccesoDenegadoException;
import com.dam.checkinn.exceptions.AltaUsuarioException;
import com.dam.checkinn.exceptions.BorradoUsuarioException;
import com.dam.checkinn.exceptions.ServerException;
import com.dam.checkinn.models.Credenciales;
import com.dam.checkinn.models.UsuarioModel;
import com.dam.checkinn.repositories.UsuarioRepository;
import com.dam.checkinn.services.UsuarioService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

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
            @RequestBody Credenciales credenciales) {
        try {
            return ResponseEntity.ok(usuarioService.login(credenciales));
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

    @PatchMapping("/usuarios")
    public ResponseEntity<UsuarioModel> updateUser(
            @RequestParam String dni,
            @RequestBody UsuarioModel usuario) {

        try {
            UsuarioModel usuarioActualizado = usuarioService.updateUser(dni, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
