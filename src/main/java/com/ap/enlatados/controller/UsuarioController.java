package com.ap.enlatados.controller;

import com.ap.enlatados.dto.LoginDTO;
import com.ap.enlatados.dto.UsuarioDTO;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.entity.Usuario;
import com.ap.enlatados.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** POST /api/usuarios  ó  /api/usuarios/registrar */
    @PostMapping(
        path = {"", "/registrar"},
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> registrarUsuario(@RequestBody @Valid UsuarioDTO dto) {
        try {
            Usuario nuevo = usuarioService.registrar(
                dto.getId(),               // ← ahora recibimos el ID desde el DTO
                dto.getNombre(),
                dto.getApellidos(),
                dto.getEmail(),
                dto.getPassword()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    /** POST /api/usuarios/login */
    @PostMapping(
        path = "/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<?> iniciarSesion(@RequestBody @Valid LoginDTO login) {
        try {
            Usuario u = usuarioService.iniciarSesion(login.getEmail(), login.getPassword());
            return ResponseEntity.ok("Bienvenido, " + u.getNombre());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    /** POST /api/usuarios/logout */
    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion() {
        usuarioService.cerrarSesion();
        return ResponseEntity.ok("Sesión cerrada");
    }

    /** PUT /api/usuarios/{id} */
    @PutMapping(
        path = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> actualizarUsuario(
        @PathVariable Long id,
        @RequestBody @Valid UsuarioDTO dto
    ) {
        try {
            Usuario actualizado = usuarioService.actualizar(
                id,
                dto.getNombre(),
                dto.getApellidos(),
                dto.getEmail(),
                dto.getPassword()
            );
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    /** DELETE /api/usuarios/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok("Usuario eliminado");
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    /** GET /api/usuarios */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    /** POST /api/usuarios/cargar-csv */
    @PostMapping(
        path = "/cargar-csv",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try {
            int cargados = usuarioService.cargarUsuariosDesdeCsv(archivo.getInputStream());
            return ResponseEntity.ok("Cargados " + cargados + " usuarios desde CSV");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error procesando CSV: " + e.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    /** GET /api/usuarios/diagrama-json */
    @GetMapping(path = "/diagrama-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DiagramDTO diagramaJson() {
        return usuarioService.obtenerDiagramaUsuariosDTO();
    }
}
