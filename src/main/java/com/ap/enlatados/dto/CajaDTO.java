package com.ap.enlatados.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CajaDTO {
    @NotBlank
    private String producto;

    @Min(1)
    private int cantidad;

    public CajaDTO() {}

    public String getProducto() { return producto; }
    public void   setProducto(String p) { this.producto = p; }

    public int    getCantidad() { return cantidad; }
    public void   setCantidad(int c)   { this.cantidad = c; }
}