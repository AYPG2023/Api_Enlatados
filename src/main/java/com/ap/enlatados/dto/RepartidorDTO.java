package com.ap.enlatados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RepartidorDTO {
    @NotBlank @Size(min = 8, max = 20)
    private String dpi;

    @NotBlank @Size(min = 2, max = 50)
    private String nombre;

    @NotBlank @Size(min = 2, max = 50)
    private String apellidos;

    @NotBlank @Size(min = 1, max = 5)
    private String tipoLicencia;

    @NotBlank @Size(min = 5, max = 20)
    private String numeroLicencia;

    @NotBlank
    @Pattern(
      regexp = "\\+?\\d{8,15}",
      message = "Teléfono debe ser un número de 8 a 15 dígitos, opcional +"
    )
    private String telefono;

    public RepartidorDTO() {}

    // --- GETTERS ---
    public String getDpi() { return dpi; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getTipoLicencia() { return tipoLicencia; }
    public String getNumeroLicencia() { return numeroLicencia; }
    public String getTelefono() { return telefono; }

    // --- SETTERS ---
    public void setDpi(String dpi) { this.dpi = dpi; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
