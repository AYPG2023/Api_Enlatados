package com.ap.enlatados.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.dto.UsuarioNode;
import com.ap.enlatados.model.Usuario;
import com.ap.enlatados.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    // Nodo interno para la lista enlazada
    private static class UsuarioNodeInternal {
        Usuario data;
        UsuarioNodeInternal next;
        UsuarioNodeInternal(Usuario u) { this.data = u; }
    }

    private UsuarioNodeInternal head;
    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        // carga inicial desde BD
        repo.findAll().forEach(this::insertarEnLista);
    }

    /** Crear + push a la lista enlazada */
    public Usuario crear(Usuario u) {
        Usuario saved = repo.save(u);
        insertarEnLista(saved);
        return saved;
    }

    /** Listar desde BD */
    public List<Usuario> listar() {
        return repo.findAll();
    }

    /** Buscar en BD */
    public Optional<Usuario> buscar(Long id) {
        return repo.findById(id);
    }

    /** Actualizar + conserva posición en lista */
    public Usuario actualizar(Long id, Usuario u) {
        return repo.findById(id)
            .map(existing -> {
                existing.setNombre(u.getNombre());
                existing.setApellidos(u.getApellidos());
                existing.setPassword(u.getPassword());
                existing.setEmail(u.getEmail());
                return repo.save(existing);
            })
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    /** Eliminar BD + pop de la lista */
    public void eliminar(Long id) {
        repo.deleteById(id);
        eliminarDeLista(id);
    }

    // -----------------------
    // manejo de lista enlazada
    // -----------------------

    private void insertarEnLista(Usuario u) {
        UsuarioNodeInternal n = new UsuarioNodeInternal(u);
        if (head == null) {
            head = n;
        } else {
            UsuarioNodeInternal t = head;
            while (t.next != null) t = t.next;
            t.next = n;
        }
    }

    private void eliminarDeLista(Long id) {
        if (head == null) return;
        if (head.data.getId().equals(id)) {
            head = head.next;
            return;
        }
        UsuarioNodeInternal t = head;
        while (t.next != null) {
            if (t.next.data.getId().equals(id)) {
                t.next = t.next.next;
                return;
            }
            t = t.next;
        }
    }

    // -------------------------------------------------
    // NUEVO: exponer lista enlazada como DTO recursivo
    // -------------------------------------------------

    /** Devuelve la cabeza de la lista enlazada como DTO */
    public UsuarioNode obtenerListaEnlazada() {
        return toDto(head);
    }

    private UsuarioNode toDto(UsuarioNodeInternal n) {
        if (n == null) return null;
        UsuarioNode dto = new UsuarioNode();
        dto.id        = n.data.getId();
        dto.nombre    = n.data.getNombre();
        dto.apellidos = n.data.getApellidos();
        dto.email     = n.data.getEmail();
        dto.next      = toDto(n.next);
        return dto;
    }
}
