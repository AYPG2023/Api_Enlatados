package com.ap.enlatados.service.eddlineales;

public class Nodo<T> {
    T data;
    Nodo<T> next;

    public Nodo(T data) {
        this.data = data;
        this.next = null;
    }

    @Override
    public String toString() {
        return "Node{" +
                "data=" + data +
                ", next=" + next +
                '}';
    }
}
