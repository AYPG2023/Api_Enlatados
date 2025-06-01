package com.ap.enlatados.entity;

import java.time.LocalDateTime;

public class Caja {
    private Long id;
    private String producto;
    private String fechaIngreso;

    public Caja(Long id) {
        this.id = id;
        this.producto = null;
        this.fechaIngreso = LocalDateTime.now().toString();
    }

    public Caja(Long id, String producto, String fechaIngreso) {
        this.id = id;
        this.producto = producto;
        this.fechaIngreso = fechaIngreso;
    }

    // Getters
    public Long getId() { return id; }
    public String getProducto() { return producto; }
    public String getFechaIngreso() { return fechaIngreso; }

    public void setProducto(String producto) {
        this.producto = producto;
    }
}
