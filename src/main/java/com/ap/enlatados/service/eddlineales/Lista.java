package com.ap.enlatados.service.eddlineales;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Lista<T> {
    private Nodo<T> head;

    /** Añade un dato al final de la lista  */
    public void add(T item) {
        Nodo<T> n = new Nodo<>(item);
        if (head == null) {
            head = n;
        } else {
            Nodo<T> t = head;
            while (t.next != null) t = t.next;
            t.next = n;
        }
    }

    /** Busca primer elemento que cumple el predicado o null */
    public T find(Predicate<T> pred) {
        Nodo<T> t = head;
        while (t != null) {
            if (pred.test(t.data)) return t.data;
            t = t.next;
        }
        return null;
    }

    /** Elimina primer elemento que cumple predicado */
    public boolean remove(Predicate<T> pred) {
        if (head == null) return false;
        if (pred.test(head.data)) {
            head = head.next;
            return true;
        }
        Nodo<T> t = head;
        while (t.next != null) {
            if (pred.test(t.next.data)) {
                t.next = t.next.next;
                return true;
            }
            t = t.next;
        }
        return false;
    }

    public List<T> toList() {
        List<T> out = new ArrayList<>();
        Nodo<T> t = head;
        while (t != null) {
            out.add(t.data);
            t = t.next;
        }
        return out;
    }

    /** Devuelve un null si la Lista esta vacia */
    public boolean isEmpty() {
        return head == null;
    }

    /**     Inserta un elemento al inicio de la lista
     */
    public void insertAtFront(T item) {
        Nodo<T> n = new Nodo<>(item);
        n.next = head;
        head = n;
    }


    /**  Elimina y devuelve el primer elemento de la lista  */
    public T removeFromFront() {
        if (head == null) return null;
        T data = head.data;
        head = head.next;
        return data;
    }

    /** Elimina y devuelve el último elemento de la lista */
    public T removeFromBack() {
        if (head == null) return null;
        if (head.next == null) {
            T data = head.data;
            head = null;
            return data;
        }
        Nodo<T> curr = head;
        while (curr.next.next != null) {
            curr = curr.next;
        }
        T data = curr.next.data;
        curr.next = null;
        return data;
    }

    /** Devuelve una representación en texto de la lista
     */
    public String print() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Nodo<T> curr = head;
        while (curr != null) {
            sb.append(curr);
            if (curr.next != null) sb.append(" → ");
            curr = curr.next;
        }
        return sb.toString();
    }
}
