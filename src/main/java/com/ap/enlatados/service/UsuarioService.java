package com.ap.enlatados.service;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    private static class UsuarioNode {
        Usuario data;
        UsuarioNode next;
        UsuarioNode(Usuario u) { this.data = u; }
    }

    private UsuarioNode head;
    private Long nextId = 1L;
    private Usuario usuarioLogueado = null;

    /** Registra un usuario nuevo; valida email único */
    public Usuario registrar(String nombre, String apellidos, String email, String password) {
        // Verificar que no exista email
        UsuarioNode t = head;
        while (t != null) {
            if (t.data.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            t = t.next;
        }
        Usuario nuevo = new Usuario(nextId++, nombre, apellidos, email, password);
        insertarEnLista(nuevo);
        return nuevo;
    }

    /** Inserta al final de la lista enlazada */
    private void insertarEnLista(Usuario u) {
        UsuarioNode node = new UsuarioNode(u);
        if (head == null) {
            head = node;
        } else {
            UsuarioNode t = head;
            while (t.next != null) t = t.next;
            t.next = node;
        }
    }

    /** Listado de todos los usuarios */
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        UsuarioNode t = head;
        while (t != null) {
            usuarios.add(t.data);
            t = t.next;
        }
        return usuarios;
    }

    /** Busca un usuario por su ID */
    public Usuario buscarPorId(Long id) {
        UsuarioNode t = head;
        while (t != null) {
            if (t.data.getId().equals(id)) {
                return t.data;
            }
            t = t.next;
        }
        throw new NoSuchElementException("Usuario no encontrado con id " + id);
    }

    /** Busca un usuario por su email */
    public Usuario buscarPorEmail(String email) {
        UsuarioNode t = head;
        while (t != null) {
            if (t.data.getEmail().equalsIgnoreCase(email)) {
                return t.data;
            }
            t = t.next;
        }
        throw new NoSuchElementException("Usuario no encontrado con email " + email);
    }

    /** Actualiza un usuario existente; valida email único si cambia */
    public Usuario actualizar(Long id,
                              String nombre,
                              String apellidos,
                              String email,
                              String password) {
        Usuario u = buscarPorId(id); // lanza NoSuchElement si no existe

        // validación de email único si cambia
        if (!u.getEmail().equalsIgnoreCase(email)) {
            UsuarioNode t = head;
            while (t != null) {
                if (t.data.getEmail().equalsIgnoreCase(email)) {
                    throw new IllegalArgumentException("El email ya está registrado");
                }
                t = t.next;
            }
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
        UsuarioNode t = head;
        while (t != null) {
            if (t.data.getEmail().equalsIgnoreCase(email)
             && t.data.getPassword().equals(password)) {
                usuarioLogueado = t.data;
                return t.data;
            }
            t = t.next;
        }
        throw new NoSuchElementException("Credenciales inválidas");
    }

    /** Cerrar sesión */
    public void cerrarSesion() {
        usuarioLogueado = null;
    }

    /** Obtener el usuario actualmente logueado */
    public Usuario obtenerPerfilActual() {
        if (usuarioLogueado == null) {
            throw new NoSuchElementException("No hay usuario logueado");
        }
        return usuarioLogueado;
    }

    /** Eliminar usuario por ID; lanza NoSuchElement si no existe */
    public void eliminar(Long id) {
        if (head == null) throw new NoSuchElementException("Usuario no encontrado con id " + id);
        if (head.data.getId().equals(id)) {
            head = head.next;
            if (usuarioLogueado != null && usuarioLogueado.getId().equals(id)) {
                usuarioLogueado = null;
            }
            return;
        }
        UsuarioNode t = head;
        while (t.next != null) {
            if (t.next.data.getId().equals(id)) {
                if (usuarioLogueado != null && usuarioLogueado.getId().equals(id)) {
                    usuarioLogueado = null;
                }
                t.next = t.next.next;
                return;
            }
            t = t.next;
        }
        throw new NoSuchElementException("Usuario no encontrado con id " + id);
    }

    /** Crear usuario con ID especificado (para CSV) */
    public Usuario crearConId(Long id, String nombre, String apellidos, String email, String password) {
        Usuario nuevo = new Usuario(id, nombre, apellidos, email, password);
        insertarEnLista(nuevo);
        if (id >= nextId) {
            nextId = id + 1;
        }
        return nuevo;
    }

    public DiagramDTO obtenerDiagramaUsuariosDTO() {
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();

        UsuarioNode t = head;
        int idx = 0;
        while (t != null) {
            // etiqueta con id y nombre
            String label = t.data.getId() + ": " + t.data.getNombre();
            nodes.add(new NodeDTO(idx, label));

            // si hay siguiente, conecta idx → idx+1
            if (t.next != null) {
                edges.add(new EdgeDTO(idx, idx + 1));
            }

            t = t.next;
            idx++;
        }

        return new DiagramDTO(nodes, edges);
    }
}
