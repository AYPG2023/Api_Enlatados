package com.ap.enlatados.model;

public class Repartidor {
    private String dpi;
    private String nombre;
    private String apellidos;
    private String licencia;
    private String telefono;

    public Repartidor(String dpi, String nombre, String apellidos, String licencia, String telefono) {
        this.dpi = dpi;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.licencia = licencia;
        this.telefono = telefono;
    }

    public String getDpi() { return dpi; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getLicencia() { return licencia; }
    public String getTelefono() { return telefono; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
