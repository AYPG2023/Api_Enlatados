package com.ap.enlatados.dto;

/**
 * DTO recursivo para representar la pila de cajas como lista enlazada.
 */
public class CajaNode {
    public Long id;
    public String fechaIngreso;
    public CajaNode next;
}
