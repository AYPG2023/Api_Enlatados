package com.ap.enlatados.service;

import java.util.List;
import java.util.NoSuchElementException;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ap.enlatados.dto.ClienteNode;
import com.ap.enlatados.model.Cliente;
import com.ap.enlatados.repository.ClienteRepository;

@Service
@Transactional
public class ClienteService {

    // Nodo interno del árbol AVL
    private static class NodoAVL {
        Cliente data;
        NodoAVL left, right;
        int height;
        NodoAVL(Cliente c) { 
            data = c; 
            height = 1; 
        }
    }

    private NodoAVL root;
    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    /** Carga inicial desde BD **/
    @PostConstruct
    private void init() {
        repo.findAll().forEach(this::insertarAVL);
    }

    /** ALTURA y FACTOR DE BALANCE **/
    private int altura(NodoAVL n) { return n == null ? 0 : n.height; }
    private int balance(NodoAVL n) { return n == null ? 0 : altura(n.left) - altura(n.right); }

    /** ROTACIONES **/
    private NodoAVL rotacionDerecha(NodoAVL y) {
        NodoAVL x = y.left;
        NodoAVL T2 = x.right;
        x.right = y; y.left = T2;
        y.height = Math.max(altura(y.left), altura(y.right)) + 1;
        x.height = Math.max(altura(x.left), altura(x.right)) + 1;
        return x;
    }
    private NodoAVL rotacionIzquierda(NodoAVL x) {
        NodoAVL y = x.right;
        NodoAVL T2 = y.left;
        y.left = x; x.right = T2;
        x.height = Math.max(altura(x.left), altura(x.right)) + 1;
        y.height = Math.max(altura(y.left), altura(y.right)) + 1;
        return y;
    }

    /** INSERT en BD + AVL **/
    public Cliente crear(Cliente c) {
        Cliente saved = repo.save(c);
        root = insertarAVLNode(root, saved);
        return saved;
    }
    private NodoAVL insertarAVLNode(NodoAVL node, Cliente c) {
        if (node == null) return new NodoAVL(c);
        int cmp = c.getDpi().compareTo(node.data.getDpi());
        if (cmp < 0) node.left = insertarAVLNode(node.left, c);
        else if (cmp > 0) node.right = insertarAVLNode(node.right, c);
        else return node;  // ya existe

        node.height = 1 + Math.max(altura(node.left), altura(node.right));
        int b = balance(node);

        // LL
        if (b > 1 && c.getDpi().compareTo(node.left.data.getDpi()) < 0)
            return rotacionDerecha(node);
        // RR
        if (b < -1 && c.getDpi().compareTo(node.right.data.getDpi()) > 0)
            return rotacionIzquierda(node);
        // LR
        if (b > 1 && c.getDpi().compareTo(node.left.data.getDpi()) > 0) {
            node.left = rotacionIzquierda(node.left);
            return rotacionDerecha(node);
        }
        // RL
        if (b < -1 && c.getDpi().compareTo(node.right.data.getDpi()) < 0) {
            node.right = rotacionDerecha(node.right);
            return rotacionIzquierda(node);
        }
        return node;
    }

    /** LISTAR TODOS **/
    public List<Cliente> listar() {
        return repo.findAll();
    }

    /** BUSCAR por ID (BD) **/
    public Cliente buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    /** BUSCAR por DPI (AVL en memoria) **/
    public Cliente buscarPorDpi(String dpi) {
        return buscarNode(root, dpi);
    }
    private Cliente buscarNode(NodoAVL node, String dpi) {
        if (node == null) return null;
        int cmp = dpi.compareTo(node.data.getDpi());
        if (cmp == 0) return node.data;
        return cmp < 0
            ? buscarNode(node.left, dpi)
            : buscarNode(node.right, dpi);
    }

    /** ACTUALIZAR **/
    public Cliente actualizar(Long id, Cliente c) {
        Cliente updated = repo.findById(id)
            .map(existing -> {
                existing.setDpi(c.getDpi());
                existing.setNombre(c.getNombre());
                existing.setApellidos(c.getApellidos());
                existing.setTelefono(c.getTelefono());
                existing.setDireccion(c.getDireccion());
                return repo.save(existing);
            })
            .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado"));
        // reconstr. AVL
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
        root = null;
        repo.findAll().forEach(this::insertarAVL);
    }

    // Helper para init y reconstr.
    private void insertarAVL(Cliente c) {
        root = insertarAVLNode(root, c);
    }

    /** --- NUEVO: Exponer el árbol AVL como DTO recursivo --- **/
    public ClienteNode obtenerArbolClientes() {
        return toDto(root);
    }

    private ClienteNode toDto(NodoAVL n) {
        if (n == null) return null;
        ClienteNode dto = new ClienteNode();
        dto.id        = n.data.getId();
        dto.dpi       = n.data.getDpi();
        dto.nombre    = n.data.getNombre();
        dto.apellidos = n.data.getApellidos();
        dto.telefono  = n.data.getTelefono();
        dto.direccion = n.data.getDireccion();
        dto.left      = toDto(n.left);
        dto.right     = toDto(n.right);
        return dto;
    }
}
