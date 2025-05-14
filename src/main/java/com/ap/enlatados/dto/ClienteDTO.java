package com.ap.enlatados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClienteDTO {
    @NotBlank @Size(min = 8, max = 20)
    private String dpi;

    @NotBlank @Size(min = 2, max = 50)
    private String nombre;

    @NotBlank @Size(min = 2, max = 50)
    private String apellidos;

    @NotBlank
    @Pattern(regexp = "\\+?\\d{8,15}", message = "Teléfono debe ser número de 8–15 dígitos")
    private String telefono;

    @NotBlank @Size(min = 5, max = 100)
    private String direccion;

    public ClienteDTO() {}

    // Getters
    public String getDpi() { return dpi; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }

    // Setters
    public void setDpi(String dpi) { this.dpi = dpi; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
