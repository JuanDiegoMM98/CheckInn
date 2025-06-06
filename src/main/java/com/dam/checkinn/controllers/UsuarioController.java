package com.dam.checkinn.controllers;

import com.dam.checkinn.exceptions.*;
import com.dam.checkinn.models.*;
import com.dam.checkinn.models.dto.alojamientos.AlojamientoDTO;
import com.dam.checkinn.models.dto.usuarios.CredencialesLoginDTO;
import com.dam.checkinn.models.dto.reservas.MisReservasDTO;
import com.dam.checkinn.models.dto.usuarios.UsuarioDTO;
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
            @RequestBody CredencialesLoginDTO credencialesLoginDTO) {
        try {
            return ResponseEntity.ok(usuarioService.login(credencialesLoginDTO));
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(401).build();
        } catch (Exception a) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioModel> me() {
        return ResponseEntity.ok(usuarioService.me());
    }

    // Crear usuario
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioModel> createUser(
            @RequestBody UsuarioModel usuario) {

        try {
            UsuarioModel nuevoUsuario = usuarioService.createUser(usuario);
            URI location = URI.create("/usuarios/" + nuevoUsuario.getId());
            return ResponseEntity
                    .created(location) // Esto automáticamente pone el código 201 Created y la cabecera Location
                    .body(nuevoUsuario);
        } catch (DatosNoValidosException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get usuario individual (area personal)
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioModel> getUsuario(
            @PathVariable int id
    ) {
        try {
            UsuarioModel usuario = usuarioService.getUserById(id);
            return ResponseEntity.ok(usuario);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioModel> deleteUser(@PathVariable int id) {
        try {
            usuarioService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Actualización
    @PatchMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioModel> updateUser(
            @PathVariable int id,
            @RequestBody UsuarioDTO dto) {

        try {
            UsuarioModel usuarioActualizado = usuarioService.updateUser(id, dto);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (AccesoDenegadoException e ){
             return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (DatosNoValidosException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Apartado mis reservas
    @GetMapping("/usuarios/{id}/reservas")
    public ResponseEntity<List<MisReservasDTO>> getReservasIdUsuario(
            @PathVariable int id
    ) {
        try {
            List<MisReservasDTO> allReservasByDniUsuario = usuarioService.getAllReservasByIdUsuario(id);
            return ResponseEntity.ok(allReservasByDniUsuario);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/usuarios/{id}/alojamientos")
    public ResponseEntity<AlojamientoModel> crearAlojamientoUsuario(
            @PathVariable int id,
            @RequestBody AlojamientoDTO alojamientoDTO
    ) {
        try {
            AlojamientoModel nuevoAlojamiento = usuarioService.guardarAlojamientoDeUsuario(id, alojamientoDTO);

            URI location = URI.create("/alojamientos/" + nuevoAlojamiento.getId());
            return ResponseEntity
                    .created(location)
                    .body(nuevoAlojamiento);
        } catch (AccesoDenegadoException e) {
            return ResponseEntity.status(403).build();
        } catch (RecursoNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (DatosNoValidosException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
