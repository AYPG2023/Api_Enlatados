package com.ap.enlatados.service.eddlineales;

import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de datos Pila (LIFO) genérica.
 */
public class Pila<T> {
    private Nodo<T> top;

    /** Inserta un elemento en la cima de la pila. */
    public void push(T item) {
        Nodo<T> n = new Nodo<>(item);
        n.next = top;
        top = n;
    }

    /** Extrae y devuelve el elemento en la cima, o null si está vacía. */
    public T pop() {
        if (top == null) return null;
        T data = top.data;
        top = top.next;
        return data;
    }

    /** Devuelve el elemento en la cima sin extraerlo, o null si está vacía. */
    public T peek() {
        return top != null ? top.data : null;
    }

    
    /** Indica si la pila está vacía. */
    public boolean isEmpty() {
        return top == null;
    }

    /** Devuelve todos los elementos de tope a fondo. */
    public List<T> toList() {
        List<T> out = new ArrayList<>();
        Nodo<T> curr = top;
        while (curr != null) {
            out.add(curr.data);
            curr = curr.next;
        }
        return out;
    }
}
