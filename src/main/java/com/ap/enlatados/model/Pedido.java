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
    private String estado = "Pendiente";
    private Cliente cliente;
    private Repartidor repartidor;
    private Vehiculo vehiculo;
    private List<CajaPedido> cajas = new ArrayList<>();

    public Pedido(String deptoOrigen, String deptoDestino, Cliente cliente, Repartidor repartidor, Vehiculo vehiculo) {
        this.numeroPedido = nextId++;
        this.deptoOrigen = deptoOrigen;
        this.deptoDestino = deptoDestino;
        this.fechaHoraInicio = LocalDateTime.now().toString();
        this.cliente = cliente;
        this.repartidor = repartidor;
        this.vehiculo = vehiculo;
    }

    public long getNumeroPedido() { return numeroPedido; }
    public String getDeptoOrigen() { return deptoOrigen; }
    public String getDeptoDestino() { return deptoDestino; }
    public String getFechaHoraInicio() { return fechaHoraInicio; }
    public String getEstado() { return estado; }
    public Cliente getCliente() { return cliente; }
    public Repartidor getRepartidor() { return repartidor; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public List<CajaPedido> getCajas() { return cajas; }
    public int getNumeroCajas() { return cajas.size(); }

    public void setEstado(String estado) { this.estado = estado; }

    public void agregarCaja(CajaPedido c) {
        cajas.add(c);
    }
}
