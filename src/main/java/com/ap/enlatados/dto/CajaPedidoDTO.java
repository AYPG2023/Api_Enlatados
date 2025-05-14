package com.ap.enlatados.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CajaPedidoDTO {
    @Min(1)
    private long numeroPedido;

    @Min(1)
    private long idCaja;

    @NotBlank
    private String fechaIngreso;

    public CajaPedidoDTO() {}

    public long getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(long numeroPedido) { this.numeroPedido = numeroPedido; }

    public long getIdCaja() { return idCaja; }
    public void setIdCaja(long idCaja) { this.idCaja = idCaja; }

    public String getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; }
}
