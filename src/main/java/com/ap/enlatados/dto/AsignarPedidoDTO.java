package com.ap.enlatados.dto;


import jakarta.validation.constraints.NotBlank;

/**
 * DTO para asignar manualmente repartidor y vehículo.
 */
public class AsignarPedidoDTO {
    @NotBlank
    private String repartidorDpi;

    @NotBlank
    private String vehiculoPlaca;

    public AsignarPedidoDTO() {}
    public String getRepartidorDpi() { return repartidorDpi; }
    public void setRepartidorDpi(String repartidorDpi) { this.repartidorDpi = repartidorDpi; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
}