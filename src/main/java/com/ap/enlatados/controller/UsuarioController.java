package com.ap.enlatados.controller;

import com.ap.enlatados.dto.*;
import com.ap.enlatados.model.Usuario;
import com.ap.enlatados.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    /**
     * Acepta POST /api/usuarios  y  POST /api/usuarios/registrar
     */
    @PostMapping(path = {"", "/registrar"}, 
                 consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registrarUsuario(@RequestBody @Valid UsuarioDTO dto) {
        try {
            Usuario nuevo = usuarioService.registrar(
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
    
    @PostMapping(path = "/login",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> iniciarSesion(@RequestBody @Valid LoginDTO login) {
        try {
            Usuario u = usuarioService.iniciarSesion(
                login.getEmail(),
                login.getPassword()
            );
            return ResponseEntity.ok("Bienvenido, " + u.getNombre());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Credenciales inválidas");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion() {
        usuarioService.cerrarSesion();
        return ResponseEntity.ok("Sesión cerrada");
    }

    /**
     * Actualiza un usuario existente.
     * PUT /api/usuarios/{id}
     */
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
                dto.getPassword()  // si es null o vacío, tu servicio puede ignorar el cambio de password
            );
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Usuario no encontrado");
        }
    }
    
    /**
     * Borra un usuario y responde 404 si no existe.
     * DELETE /api/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok("Usuario eliminado");
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Usuario no encontrado");
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Usuario> listar() {
        return usuarioService.listar();
    }


    @PostMapping(path = "/cargar-csv",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))
        ) {
            String linea;
            int contador = 0;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(";");
                if (p.length != 5) continue;
                usuarioService.crearConId(
                    Long.parseLong(p[0].trim()),
                    p[1].trim(),
                    p[2].trim(),
                    p[3].trim(),
                    p[4].trim()
                );
                contador++;
            }
            return ResponseEntity.ok("Cargados " + contador + " usuarios desde CSV");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Error procesando CSV: " + e.getMessage());
        }
    }

    @GetMapping(path = "/diagrama-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiagramDTO> diagramaJson() {
        DiagramDTO dto = usuarioService.obtenerDiagramaUsuariosDTO();
        return ResponseEntity.ok(dto);
    }
}
