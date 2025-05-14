// src/main/java/com/ap/enlatados/dto/PedidoDTO.java
package com.ap.enlatados.dto;

import jakarta.validation.constraints.NotBlank;

public class PedidoDTO {
    @NotBlank
    private String deptoOrigen;

    @NotBlank
    private String deptoDestino;

    @NotBlank
    private String dpiCliente;

    public PedidoDTO() {}

    public String getDeptoOrigen() { return deptoOrigen; }
    public void setDeptoOrigen(String deptoOrigen) { this.deptoOrigen = deptoOrigen; }

    public String getDeptoDestino() { return deptoDestino; }
    public void setDeptoDestino(String deptoDestino) { this.deptoDestino = deptoDestino; }

    public String getDpiCliente() { return dpiCliente; }
    public void setDpiCliente(String dpiCliente) { this.dpiCliente = dpiCliente; }
}
