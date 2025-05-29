package com.ap.enlatados.service.eddlineales;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Lista<T> {
    private Nodo<T> head;

    /** Añade al final */
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

    /** Devuelve todo en List */
    public List<T> toList() {
        List<T> out = new ArrayList<>();
        Nodo<T> t = head;
        while (t != null) {
            out.add(t.data);
            t = t.next;
        }
        return out;
    }
}
