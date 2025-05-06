package com.ap.enlatados.dto;

/**
 * Nodo recursivo para exponer la cola de repartidores.
 */
public class RepartidorNode {
    public Long id;
    public String dpi;
    public String nombre;
    public String apellidos;
    public String licencia;
    public String telefono;
    public RepartidorNode next;
}
