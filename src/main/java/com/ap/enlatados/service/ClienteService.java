package com.ap.enlatados.service;

import com.ap.enlatados.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteService {

    private static class NodoAVL {
        Cliente data;
        NodoAVL left, right;
        int height;
        NodoAVL(Cliente c) { data = c; height = 1; }
    }

    private NodoAVL root;

    private int altura(NodoAVL n) { return n == null ? 0 : n.height; }
    private int balance(NodoAVL n) { return n == null ? 0 : altura(n.left) - altura(n.right); }

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

    public void crear(Cliente c) {
        root = insertarAVLNode(root, c);
    }

    private NodoAVL insertarAVLNode(NodoAVL node, Cliente c) {
        if (node == null) return new NodoAVL(c);
        int cmp = c.getDpi().compareTo(node.data.getDpi());
        if (cmp < 0) node.left = insertarAVLNode(node.left, c);
        else if (cmp > 0) node.right = insertarAVLNode(node.right, c);
        else throw new IllegalArgumentException("Cliente ya existe");
        node.height = 1 + Math.max(altura(node.left), altura(node.right));
        return balancear(node, c);
    }

    private NodoAVL balancear(NodoAVL node, Cliente c) {
        int b = balance(node);
        if (b > 1 && c.getDpi().compareTo(node.left.data.getDpi()) < 0) return rotacionDerecha(node);
        if (b < -1 && c.getDpi().compareTo(node.right.data.getDpi()) > 0) return rotacionIzquierda(node);
        if (b > 1 && c.getDpi().compareTo(node.left.data.getDpi()) > 0) {
            node.left = rotacionIzquierda(node.left);
            return rotacionDerecha(node);
        }
        if (b < -1 && c.getDpi().compareTo(node.right.data.getDpi()) < 0) {
            node.right = rotacionDerecha(node.right);
            return rotacionIzquierda(node);
        }
        return node;
    }

    public Cliente buscar(String dpi) {
        return buscarNode(root, dpi);
    }

    private Cliente buscarNode(NodoAVL node, String dpi) {
        if (node == null) throw new NoSuchElementException("Cliente no encontrado");
        int cmp = dpi.compareTo(node.data.getDpi());
        if (cmp == 0) return node.data;
        return cmp < 0 ? buscarNode(node.left, dpi) : buscarNode(node.right, dpi);
    }

    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        inorder(root, clientes);
        return clientes;
    }

    private void inorder(NodoAVL node, List<Cliente> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.data);
        inorder(node.right, out);
    }

    public void actualizar(String dpi, Cliente nuevo) {
        eliminar(dpi);
        crear(nuevo);
    }

    public void eliminar(String dpi) {
        root = eliminarAVLNode(root, dpi);
    }

    private NodoAVL eliminarAVLNode(NodoAVL node, String dpi) {
        if (node == null) throw new NoSuchElementException("Cliente no encontrado");
        int cmp = dpi.compareTo(node.data.getDpi());
        if (cmp < 0) node.left = eliminarAVLNode(node.left, dpi);
        else if (cmp > 0) node.right = eliminarAVLNode(node.right, dpi);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            NodoAVL min = obtenerMin(node.right);
            node.data = min.data;
            node.right = eliminarAVLNode(node.right, min.data.getDpi());
        }
        node.height = 1 + Math.max(altura(node.left), altura(node.right));
        return balancear(node, node.data);
    }

    private NodoAVL obtenerMin(NodoAVL node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public void cargarMasivo(List<String[]> datos) {
        for (String[] linea : datos) {
            if (linea.length != 5) continue;
            crear(new Cliente(linea[0].trim(), linea[1].trim(), linea[2].trim(), linea[3].trim(), linea[4].trim()));
        }
    }

    /** Diagrama AVL en texto */
    public String obtenerDiagramaAVL() {
        StringBuilder sb = new StringBuilder();
        diagramaAVL(root, sb, 0);
        return sb.toString();
    }

    private void diagramaAVL(NodoAVL node, StringBuilder sb, int nivel) {
        if (node == null) return;
        diagramaAVL(node.right, sb, nivel + 1);
        sb.append("   ".repeat(nivel)).append(node.data.getDpi())
          .append(" (").append(node.data.getDireccion()).append(")").append("\n");
        diagramaAVL(node.left, sb, nivel + 1);
    }
}
