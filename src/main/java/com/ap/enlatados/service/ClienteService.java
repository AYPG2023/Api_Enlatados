package com.ap.enlatados.service;

import java.util.List;
import java.util.NoSuchElementException;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.repository.ClienteRepository;

@Service
@Transactional
public class ClienteService {

    // Nodo interno del Árbol AVL
    private static class NodoAVL {
        Cliente data;
        NodoAVL left, right;
        int height;
        NodoAVL(Cliente c) { 
            this.data = c; 
            this.height = 1; 
        }
    }

    private NodoAVL root;
    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    /** Inicializa el árbol cargando todos los clientes de la BD **/
    @PostConstruct
    private void init() {
        repo.findAll().forEach(this::insertarAVL);
    }

    /** ALTURA y FACTOR DE BALANCE **/
    private int altura(NodoAVL n) {
        return (n == null) ? 0 : n.height;
    }
    private int balance(NodoAVL n) {
        return (n == null) ? 0 : altura(n.left) - altura(n.right);
    }

    /** ROTACIONES **/
    private NodoAVL rotacionDerecha(NodoAVL y) {
        NodoAVL x = y.left;
        NodoAVL T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = Math.max(altura(y.left), altura(y.right)) + 1;
        x.height = Math.max(altura(x.left), altura(x.right)) + 1;
        return x;
    }
    private NodoAVL rotacionIzquierda(NodoAVL x) {
        NodoAVL y = x.right;
        NodoAVL T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = Math.max(altura(x.left), altura(x.right)) + 1;
        y.height = Math.max(altura(y.left), altura(y.right)) + 1;
        return y;
    }

    /** INSERT en AVL y BD **/
    public Cliente crear(Cliente c) {
        Cliente saved = repo.save(c);
        root = insertarAVLNode(root, saved);
        return saved;
    }

    private NodoAVL insertarAVLNode(NodoAVL node, Cliente c) {
        if (node == null) {
            return new NodoAVL(c);
        }
        int cmp = c.getDpi().compareTo(node.data.getDpi());
        if (cmp < 0) {
            node.left = insertarAVLNode(node.left, c);
        } else if (cmp > 0) {
            node.right = insertarAVLNode(node.right, c);
        } else {
            // DPI duplicado: no insertar
            return node;
        }

        // Actualizar altura
        node.height = 1 + Math.max(altura(node.left), altura(node.right));

        // Balance
        int b = balance(node);

        // Casos de rotación
        if (b > 1 && c.getDpi().compareTo(node.left.data.getDpi()) < 0) {
            return rotacionDerecha(node);               // LL
        }
        if (b < -1 && c.getDpi().compareTo(node.right.data.getDpi()) > 0) {
            return rotacionIzquierda(node);             // RR
        }
        if (b > 1 && c.getDpi().compareTo(node.left.data.getDpi()) > 0) {
            node.left = rotacionIzquierda(node.left);   // LR
            return rotacionDerecha(node);
        }
        if (b < -1 && c.getDpi().compareTo(node.right.data.getDpi()) < 0) {
            node.right = rotacionDerecha(node.right);   // RL
            return rotacionIzquierda(node);
        }

        return node;
    }

    /** LISTAR TODOS **/
    public List<Cliente> listar() {
        return repo.findAll();
    }

    /** BUSCAR por ID **/
    public Cliente buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    /** BUSCAR por DPI (árbol AVL en memoria) **/
    public Cliente buscarPorDpi(String dpi) {
        return buscarNode(root, dpi);
    }
    private Cliente buscarNode(NodoAVL node, String dpi) {
        if (node == null) {
            return null;
        }
        int cmp = dpi.compareTo(node.data.getDpi());
        if (cmp == 0) {
            return node.data;
        } else if (cmp < 0) {
            return buscarNode(node.left, dpi);
        } else {
            return buscarNode(node.right, dpi);
        }
    }

    /** ACTUALIZAR **/
    public Cliente actualizar(Long id, Cliente c) {
        Cliente updated = repo.findById(id).map(existing -> {
            existing.setDpi(c.getDpi());
            existing.setNombre(c.getNombre());
            existing.setApellidos(c.getApellidos());
            existing.setTelefono(c.getTelefono());
            existing.setDireccion(c.getDireccion());
            return repo.save(existing);
        }).orElseThrow(() -> new NoSuchElementException("Cliente no encontrado"));

        // Reconstruir AVL
        root = null;
        repo.findAll().forEach(this::insertarAVL);
        return updated;
    }

    /** ELIMINAR **/
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Cliente no encontrado");
        }
        repo.deleteById(id);
        // Reconstruir AVL
        root = null;
        repo.findAll().forEach(this::insertarAVL);
    }

    /** Helper para init y reconstrucción **/
    private void insertarAVL(Cliente c) {
        root = insertarAVLNode(root, c);
    }
}
