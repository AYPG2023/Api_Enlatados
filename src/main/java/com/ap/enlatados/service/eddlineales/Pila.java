package com.ap.enlatados.service.eddlineales;

import java.util.ArrayList;
import java.util.List;
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


    /** Devuelve un null si ña pila esta vacia  */
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * Alias de pop: elimina y devuelve el elemento que está hasta arriba de la pila
     * (o retorna null si la pila está vacía)
     */
    public T popTop() {
        return pop();
    }

    /** Devuelve toda la info de la pila */
    public String print() {
        StringBuilder sb = new StringBuilder();
        Nodo<T> curr = top;
        while (curr != null) {
            sb.append(curr);
            if (curr.next != null) sb.append(" → ");
            curr = curr.next;
        }
        return sb.toString();
    }

    /** Devuelve todos los elementos de tope a fondo como List<T>. */
    public List<T> toList() {
        List<T> out = new ArrayList<>();
        Nodo<T> curr = top;
        while (curr != null) {
            out.add(curr.data);
            curr = curr.next;
        }
        return out;
    }

    @Override
    public String toString() {
        return print();
    }
}
