package com.ap.enlatados.model;

public class CajaPedido {
    private Long id;
    private String fechaIngreso;

    public CajaPedido(Long id, String fechaIngreso) {
        this.id = id;
        this.fechaIngreso = fechaIngreso;
    }

    public Long getId() {
        return id;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }
}
