package com.ap.enlatados.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private static long nextId = 1L;

    private long numeroPedido;
    private String deptoOrigen;
    private String deptoDestino;
    private String fechaHoraInicio;
    private String estado;              // "Pendiente", "EnCurso", "Completado"
    private Cliente cliente;
    private Repartidor repartidor;      // null si no hay disponible
    private Vehiculo vehiculo;          // null si no hay disponible
    private List<CajaPedido> cajas = new ArrayList<>();

    public Pedido(String deptoOrigen, String deptoDestino,
                  Cliente cliente,
                  Repartidor repartidor,
                  Vehiculo vehiculo) {
        this.numeroPedido    = nextId++;
        this.deptoOrigen     = deptoOrigen;
        this.deptoDestino    = deptoDestino;
        this.fechaHoraInicio = LocalDateTime.now().toString();
        this.cliente         = cliente;
        this.repartidor      = repartidor;
        this.vehiculo        = vehiculo;
        // Estado inicial según recursos asignados
        this.estado = (repartidor != null && vehiculo != null)
                      ? "EnCurso"
                      : "Pendiente";
    }

    // Getters
    public long getNumeroPedido()   { return numeroPedido; }
    public String getDeptoOrigen()  { return deptoOrigen; }
    public String getDeptoDestino() { return deptoDestino; }
    public String getFechaHoraInicio() { return fechaHoraInicio; }
    public String getEstado()       { return estado; }
    public Cliente getCliente()     { return cliente; }
    public Repartidor getRepartidor() { return repartidor; }
    public Vehiculo getVehiculo()     { return vehiculo; }
    public List<CajaPedido> getCajas() { return cajas; }
    public int getNumeroCajas()     { return cajas.size(); }

    // Setters que actualizan estado a EnCurso si ambos recursos están
    public void setRepartidor(Repartidor r) {
        this.repartidor = r;
        if (this.vehiculo != null) {
            this.estado = "EnCurso";
        }
    }

    public void setVehiculo(Vehiculo v) {
        this.vehiculo = v;
        if (this.repartidor != null) {
            this.estado = "EnCurso";
        }
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void agregarCaja(CajaPedido c) {
        cajas.add(c);
    }
}
