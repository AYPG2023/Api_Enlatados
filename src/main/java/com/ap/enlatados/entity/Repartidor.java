package com.ap.enlatados.entity;

public class Repartidor {
    private String dpi;
    private String nombre;
    private String apellidos;
    private String tipoLicencia;
    private String numeroLicencia;
    private String telefono;

    public Repartidor(
        String dpi,
        String nombre,
        String apellidos,
        String tipoLicencia,
        String numeroLicencia,
        String telefono
    ) {
        this.dpi = dpi;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.tipoLicencia = tipoLicencia;
        this.numeroLicencia = numeroLicencia;
        this.telefono = telefono;
    }

    // Getters
    public String getDpi()            { return dpi; }
    public String getNombre()         { return nombre; }
    public String getApellidos()      { return apellidos; }
    public String getTipoLicencia()   { return tipoLicencia; }
    public String getNumeroLicencia(){ return numeroLicencia; }
    public String getTelefono()       { return telefono; }

    // Setters (except dpi)
    public void setNombre(String nombre)             { this.nombre = nombre; }
    public void setApellidos(String apellidos)       { this.apellidos = apellidos; }
    public void setTipoLicencia(String tipoLicencia) { this.tipoLicencia = tipoLicencia; }
    public void setNumeroLicencia(String num)        { this.numeroLicencia = num; }
    public void setTelefono(String telefono)         { this.telefono = telefono; }
}
