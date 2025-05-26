package com.ap.enlatados.entity;

import java.time.LocalDateTime;

public class Caja {
    private Long id;
    private String producto;
    private String fechaIngreso;

    // Constructor original manteniéndolo para backward-compatibility
    public Caja(Long id) {
        this.id = id;
        this.producto = null;
        this.fechaIngreso = LocalDateTime.now().toString();
    }

    // Nuevo constructor completo
    public Caja(Long id, String producto, String fechaIngreso) {
        this.id = id;
        this.producto = producto;
        this.fechaIngreso = fechaIngreso;
    }

    // Getters
    public Long getId() { return id; }
    public String getProducto() { return producto; }
    public String getFechaIngreso() { return fechaIngreso; }

    // (Opcional) Setter de producto si lo necesitas
    public void setProducto(String producto) {
        this.producto = producto;
    }
}
