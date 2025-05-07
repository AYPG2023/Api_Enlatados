package com.ap.enlatados.dto;

/**
 * Nodo recursivo para exponer la cola de vehículos.
 */
public class LinkedVehiculoNode {
    public Long id;
    public String placa;
    public String marca;
    public String modelo;
    public String color;
    public int anio;
    public String transmision;
    public LinkedVehiculoNode next;
}
