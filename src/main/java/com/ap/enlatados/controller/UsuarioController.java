package com.ap.enlatados.controller;

import com.ap.enlatados.model.Usuario;
import com.ap.enlatados.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.registrar(
                usuario.getNombre(), usuario.getApellidos(), usuario.getEmail(), usuario.getPassword()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody Usuario usuario) {
        try {
            Usuario u = usuarioService.iniciarSesion(usuario.getEmail(), usuario.getPassword());
            return ResponseEntity.ok("Inicio de sesión exitoso. Bienvenido: " + u.getNombre());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> cerrarSesion() {
        usuarioService.cerrarSesion();
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> verPerfilActual() {
        try {
            Usuario u = usuarioService.obtenerPerfilActual();
            return ResponseEntity.ok(u);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No hay usuario logueado");
        }
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado");
    }

    @PostMapping("/cargar-csv")
    public ResponseEntity<?> cargarDesdeCsv(@RequestParam("archivo") MultipartFile archivo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            int contador = 0;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length != 5) continue;
                Long id = Long.parseLong(partes[0].trim());
                String nombre = partes[1].trim();
                String apellidos = partes[2].trim();
                String email = partes[3].trim();
                String password = partes[4].trim();
                usuarioService.crearConId(id, nombre, apellidos, email, password);
                contador++;
            }
            return ResponseEntity.ok("Se cargaron " + contador + " usuarios desde CSV.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/linked")
    public ResponseEntity<?> obtenerListaEnlazadaComoNodos() {
        List<Usuario> usuarios = usuarioService.listar();
        StringBuilder sb = new StringBuilder();
        for (Usuario u : usuarios) {
            sb.append("[").append(u.getId()).append(": ").append(u.getNombre()).append("] -> ");
        }
        sb.append("NULL");
        return ResponseEntity.ok(sb.toString());
    }
}
