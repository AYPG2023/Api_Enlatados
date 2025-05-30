package com.ap.enlatados.service;

import com.ap.enlatados.entity.Usuario;
import com.ap.enlatados.service.eddlineales.Lista;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.dto.NodeDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final Lista<Usuario> lista = new Lista<>();
    private Usuario usuarioLogueado = null;

    /**
     * Devuelve el mayor ID actualmente registrado en la lista (0 si está vacía).
     */
    private Long obtenerMaxId() {
        Long max = 0L;
        for (Usuario u : listar()) {
            if (u.getId() != null && u.getId() > max) {
                max = u.getId();
            }
        }
        return max;
    }

    /**
     * Devuelve el siguiente ID sugerido (maxId + 1).
     */
    public Long obtenerProximoId() {
        return obtenerMaxId() + 1;
    }

    /**
     * Registra un usuario nuevo; el ID se pasa desde el DTO
     */
    public Usuario registrar(Long id,
                             String nombre,
                             String apellidos,
                             String email,
                             String password) {
        // validar ID único
        if (lista.find(u -> u.getId().equals(id)) != null) {
            Long maxId = obtenerMaxId();
            throw new IllegalArgumentException(
                    "El ID " + id + " ya existe."
                            + " Usa un ID mayor o igual a "
                            + (maxId + 1) + "."
            );
        }
        // validar email único
        if (lista.find(u -> u.getEmail().equalsIgnoreCase(email)) != null) {
            throw new IllegalArgumentException("Pedro wueco");
        }
        Usuario u = new Usuario(id, nombre, apellidos, email, password);
        lista.add(u);
        return u;
    }

    /** Lista todos los usuarios */
    public List<Usuario> listar() {
        return lista.toList();
    }

    /** Busca un usuario por su ID */
    public Usuario buscarPorId(Long id) {
        Usuario u = lista.find(x -> x.getId().equals(id));
        if (u == null) throw new NoSuchElementException("Usuario no encontrado con id " + id);
        return u;
    }

    /** Busca un usuario por su email */
    public Usuario buscarPorEmail(String email) {
        Usuario u = lista.find(x -> x.getEmail().equalsIgnoreCase(email));
        if (u == null) throw new NoSuchElementException("Usuario no encontrado con email " + email);
        return u;
    }

    /** Actualiza un usuario existente (no cambia el ID) */
    public Usuario actualizar(Long id,
                              String nombre,
                              String apellidos,
                              String email,
                              String password) {
        Usuario u = buscarPorId(id);
        // si cambió email, validar unicidad
        if (!u.getEmail().equalsIgnoreCase(email)
                && lista.find(x -> x.getEmail().equalsIgnoreCase(email)) != null) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setEmail(email);
        if (password != null && !password.isBlank()) {
            u.setPassword(password);
        }
        return u;
    }

    /** Iniciar sesión: valida email+password */
    public Usuario iniciarSesion(String email, String password) {
        Usuario u = lista.find(x ->
            x.getEmail().equalsIgnoreCase(email) &&
            x.getPassword().equals(password)
        );
        if (u == null) throw new NoSuchElementException("Credenciales inválidas");
        usuarioLogueado = u;
        return u;
    }

    /** Cerrar sesión */
    public void cerrarSesion() {
        usuarioLogueado = null;
    }

    /** Perfil del usuario actualmente logueado */
    public Usuario obtenerPerfilActual() {
        if (usuarioLogueado == null) {
            throw new NoSuchElementException("No hay usuario logueado");
        }
        return usuarioLogueado;
    }

    /** Elimina un usuario por ID */
    public void eliminar(Long id) {
        boolean removed = lista.remove(x -> x.getId().equals(id));
        if (!removed) {
            throw new NoSuchElementException("Usuario no encontrado con id " + id);
        }
        // si era el logueado, lo cerramos
        if (usuarioLogueado != null && usuarioLogueado.getId().equals(id)) {
            usuarioLogueado = null;
        }
    }

    /**
     * Carga un CSV con cabecera:
     * Id;Nombre;Apellido;Email;Contraseña
     * y registra cada usuario usando el ID del archivo.
     */
    public int cargarUsuariosDesdeCsv(InputStream is) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(';')
            .withIgnoreEmptyLines()
            .withTrim();

        try (
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, format)
        ) {
            int count = 0;
            for (CSVRecord record : parser) {
                Long id = Long.parseLong(record.get("id"));
                String nombre    = record.get("nombre");
                String apellidos = record.get("apellidos");
                String email     = record.get("email");
                String password  = record.get("password");
                registrar(id, nombre, apellidos, email, password);
                count++;
            }
            return count;
        }
    }

    /** Genera el DTO de diagrama (lista enlazada) mostrando solo el ID */
    public DiagramDTO obtenerDiagramaUsuariosDTO() {
        List<NodeDTO> nodes = lista.toList().stream()
            .map(u -> new NodeDTO(
                u.getId().intValue(),       // id como entero
                String.valueOf(u.getId())   // etiqueta solo con el ID
            ))
            .collect(Collectors.toList());

        List<EdgeDTO> edges = nodes.stream()
            .filter(n -> n.getId() < nodes.size() - 1)
            .map(n -> new EdgeDTO(n.getId(), n.getId() + 1))
            .collect(Collectors.toList());

        return new DiagramDTO(nodes, edges);
    }

}
