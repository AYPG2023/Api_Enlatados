package com.ap.enlatados.model;

public class Vehiculo {
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private int anio;
    private String transmision;

    public Vehiculo(String placa, String marca, String modelo, String color, int anio, String transmision) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.anio = anio;
        this.transmision = transmision;
    }

    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getColor() { return color; }
    public int getAnio() { return anio; }
    public String getTransmision() { return transmision; }

    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setColor(String color) { this.color = color; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setTransmision(String transmision) { this.transmision = transmision; }
}
