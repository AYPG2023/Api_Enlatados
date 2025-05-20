package com.ap.enlatados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public class PedidoDTO {
    @NotBlank
    private String deptoOrigen;
    @NotBlank
    private String deptoDestino;
    @NotBlank
    private String dpiCliente;
    @NotEmpty
    private List<PedidoItemDTO> items;

    @Pattern(regexp="AUTO|MANUAL")
    private String tipoAsignacion; // AUTO o MANUAL
    private String repartidorDpi;   // solo si MANUAL
    private String vehiculoPlaca;   // solo si MANUAL

    public PedidoDTO() {}

    public String getDeptoOrigen()     { return deptoOrigen; }
    public void   setDeptoOrigen(String deptoOrigen) { this.deptoOrigen = deptoOrigen; }

    public String getDeptoDestino()    { return deptoDestino; }
    public void   setDeptoDestino(String deptoDestino) { this.deptoDestino = deptoDestino; }

    public String getDpiCliente()      { return dpiCliente; }
    public void   setDpiCliente(String dpiCliente) { this.dpiCliente = dpiCliente; }

    public List<PedidoItemDTO> getItems() { return items; }
    public void               setItems(List<PedidoItemDTO> items) { this.items = items; }

    public String getTipoAsignacion()  { return tipoAsignacion; }
    public void   setTipoAsignacion(String tipoAsignacion) { this.tipoAsignacion = tipoAsignacion; }

    public String getRepartidorDpi()   { return repartidorDpi; }
    public void   setRepartidorDpi(String repartidorDpi) { this.repartidorDpi = repartidorDpi; }

    public String getVehiculoPlaca()   { return vehiculoPlaca; }
    public void   setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
}
