package com.ap.enlatados.service;

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

    public Usuario registrar(String nombre, String apellidos, String email, String password) {
        // Validación de email único
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

    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        UsuarioNode t = head;
        while (t != null) {
            usuarios.add(t.data);
            t = t.next;
        }
        return usuarios;
    }

    public Usuario buscarPorId(Long id) {
        UsuarioNode t = head;
        while (t != null) {
            if (t.data.getId().equals(id)) return t.data;
            t = t.next;
        }
        throw new NoSuchElementException("Usuario no encontrado");
    }

    public Usuario buscarPorEmail(String email) {
        UsuarioNode t = head;
        while (t != null) {
            if (t.data.getEmail().equalsIgnoreCase(email)) return t.data;
            t = t.next;
        }
        throw new NoSuchElementException("Usuario no encontrado");
    }

    public Usuario iniciarSesion(String email, String password) {
        UsuarioNode t = head;
        while (t != null) {
            if (t.data.getEmail().equalsIgnoreCase(email) && t.data.getPassword().equals(password)) {
                usuarioLogueado = t.data;
                return t.data;
            }
            t = t.next;
        }
        throw new NoSuchElementException("Credenciales inválidas");
    }

    public void cerrarSesion() {
        usuarioLogueado = null;
    }

    public Usuario obtenerPerfilActual() {
        if (usuarioLogueado == null) throw new NoSuchElementException("No hay usuario logueado");
        return usuarioLogueado;
    }

    public void eliminar(Long id) {
        if (head == null) return;
        if (head.data.getId().equals(id)) {
            head = head.next;
            return;
        }
        UsuarioNode t = head;
        while (t.next != null) {
            if (t.next.data.getId().equals(id)) {
                t.next = t.next.next;
                return;
            }
            t = t.next;
        }
    }

    public Usuario crearConId(Long id, String nombre, String apellidos, String email, String password) {
        Usuario nuevo = new Usuario(id, nombre, apellidos, email, password);
        insertarEnLista(nuevo);
        if (id >= nextId) nextId = id + 1;
        return nuevo;
    }
}
