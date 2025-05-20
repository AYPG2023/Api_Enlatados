package com.ap.enlatados.dto;

import jakarta.validation.constraints.*;

public class VehiculoDTO {
	 @NotBlank @Size(min = 4, max = 8)
	    private String placa;

	    @NotBlank @Size(max = 50)
	    private String marca;

	    @NotBlank @Size(max = 50)
	    private String modelo;

	    @NotBlank @Size(max = 30)
	    private String color;

	    @Min(1900) @Max(2100)
	    private int anio;

	    @NotBlank @Size(max = 20)
	    private String transmision;

    @NotBlank
    @Pattern(
      regexp = "CARRO|MOTO|REMOLQUE|BUS_URBANO|BUS_EXTRAURBANO|CD|CC|MI|O|P|A",
      message = "TipoVehiculo inválido"
    )
    private String tipoVehiculo;

    public VehiculoDTO() {}

    // Getters
    public String getPlaca()         { return placa; }
    public String getMarca()         { return marca; }
    public String getModelo()        { return modelo; }
    public String getColor()         { return color; }
    public int    getAnio()          { return anio; }
    public String getTransmision()   { return transmision; }
    public String getTipoVehiculo()  { return tipoVehiculo; }

    // Setters
    public void setPlaca(String placa)               { this.placa = placa; }
    public void setMarca(String marca)               { this.marca = marca; }
    public void setModelo(String modelo)             { this.modelo = modelo; }
    public void setColor(String color)               { this.color = color; }
    public void setAnio(int anio)                    { this.anio = anio; }
    public void setTransmision(String transmision)   { this.transmision = transmision; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }
}
