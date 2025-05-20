package com.ap.enlatados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO para crear un pedido.
 */
public class PedidoDTO {
    @NotBlank
    private String deptoOrigen;

    @NotBlank
    private String deptoDestino;

    @NotBlank
    private String dpiCliente;

    @NotEmpty
    private List<PedidoItemDTO> items;

    public PedidoDTO() {}
    public String getDeptoOrigen() { return deptoOrigen; }
    public void setDeptoOrigen(String deptoOrigen) { this.deptoOrigen = deptoOrigen; }
    public String getDeptoDestino() { return deptoDestino; }
    public void setDeptoDestino(String deptoDestino) { this.deptoDestino = deptoDestino; }
    public String getDpiCliente() { return dpiCliente; }
    public void setDpiCliente(String dpiCliente) { this.dpiCliente = dpiCliente; }
    public List<PedidoItemDTO> getItems() { return items; }
    public void setItems(List<PedidoItemDTO> items) { this.items = items; }
}