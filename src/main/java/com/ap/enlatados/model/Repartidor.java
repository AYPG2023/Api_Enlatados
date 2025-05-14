package com.ap.enlatados.model;

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

    // --- GETTERS ---
    public String getDpi()           { return dpi; }
    public String getNombre()        { return nombre; }
    public String getApellidos()     { return apellidos; }
    public String getTipoLicencia()  { return tipoLicencia; }
    public String getNumeroLicencia(){ return numeroLicencia; }
    public String getTelefono()      { return telefono; }

    // --- SETTERS ---
    public void setNombre(String n)             { this.nombre = n; }
    public void setApellidos(String a)          { this.apellidos = a; }
    public void setTipoLicencia(String t)       { this.tipoLicencia = t; }
    public void setNumeroLicencia(String num)   { this.numeroLicencia = num; }
    public void setTelefono(String tel)         { this.telefono = tel; }
}
