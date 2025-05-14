package com.ap.enlatados.dto;

import jakarta.validation.constraints.*;

public class VehiculoDTO {
    @NotBlank @Size(min = 4, max = 8)
    private String placa;

    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;

    @NotBlank
    private String color;

    @Min(1900) @Max(2100)
    private int anio;

    @NotBlank
    private String transmision;

    public VehiculoDTO() {}

    // getters
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getColor() { return color; }
    public int getAnio() { return anio; }
    public String getTransmision() { return transmision; }

    // setters
    public void setPlaca(String placa) { this.placa = placa; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setColor(String color) { this.color = color; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setTransmision(String transmision) { this.transmision = transmision; }
}
