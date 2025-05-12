package com.ap.enlatados.model;

public class Cliente {
    private String dpi; // Clave principal
    private String nombre;
    private String apellidos;
    private String telefono;

    public Cliente(String dpi, String nombre, String apellidos, String telefono) {
        this.dpi = dpi;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }

    public String getDpi() { return dpi; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getTelefono() { return telefono; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
