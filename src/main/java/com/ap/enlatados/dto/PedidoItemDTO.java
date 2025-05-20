package com.ap.enlatados.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class PedidoItemDTO {
    @NotBlank
    private String producto;

    @Min(1)
    private int cantidad;

    public PedidoItemDTO() {}
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
