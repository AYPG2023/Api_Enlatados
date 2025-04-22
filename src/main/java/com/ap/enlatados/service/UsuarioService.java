package com.ap.enlatados.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.model.Usuario;
import com.ap.enlatados.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private static class Nodo {
        Usuario data;
        Nodo next;
        Nodo(Usuario u){ this.data = u; }
    }

    private Nodo head;
    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    private void init() {
        // Carga inicial en la lista enlazada desde la BD
        repo.findAll().forEach(this::insertarEnLista);
    }

    /** Crea un nuevo usuario (y lo añade a la lista enlazada) **/
    public Usuario crear(Usuario u) {
        // u debe traer nombre, apellidos, password y email
        Usuario saved = repo.save(u);
        insertarEnLista(saved);
        return saved;
    }

    /** Lista todos los usuarios desde la BD **/
    public List<Usuario> listar() {
        return repo.findAll();
    }

    /** Busca un usuario por su ID **/
    public Optional<Usuario> buscar(Long id) {
        return repo.findById(id);
    }

    /** Actualiza un usuario existente **/
    public Usuario actualizar(Long id, Usuario u) {
        return repo.findById(id)
            .map(existing -> {
                existing.setNombre(u.getNombre());
                existing.setApellidos(u.getApellidos());
                existing.setPassword(u.getPassword());
                existing.setEmail(u.getEmail());         // <-- actualizar email
                return repo.save(existing);
            })
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    /** Elimina un usuario (BD + lista enlazada) **/
    public void eliminar(Long id) {
        repo.deleteById(id);
        eliminarDeLista(id);
    }

    // -----------------------
    // Métodos internos LIFO
    // -----------------------

    private void insertarEnLista(Usuario u) {
        Nodo node = new Nodo(u);
        if (head == null) {
            head = node;
        } else {
            Nodo t = head;
            while (t.next != null) {
                t = t.next;
            }
            t.next = node;
        }
    }

    private void eliminarDeLista(Long id) {
        if (head == null) return;
        if (head.data.getId().equals(id)) {
            head = head.next;
            return;
        }
        Nodo t = head;
        while (t.next != null) {
            if (t.next.data.getId().equals(id)) {
                t.next = t.next.next;
                return;
            }
            t = t.next;
        }
    }
}
