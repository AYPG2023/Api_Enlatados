package com.ap.enlatados.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Pedido {

	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;                             // número de pedido

	    private String deptoOrigen;
	    private String deptoDestino;
	    private String fechaHoraInicio;

	    private int  numeroCajas;                    // se actualiza al asignar cajas
	    private String estado = "Pendiente";         // “Pendiente” por defecto

	    /* --- Relaciones simples (FK) --- */
	    @ManyToOne(fetch = FetchType.LAZY)
	    private Cliente cliente;

	    @ManyToOne(fetch = FetchType.LAZY)
	    private Repartidor repartidor;

	    @ManyToOne(fetch = FetchType.LAZY)
	    private Vehiculo vehiculo;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getDeptoOrigen() {
			return deptoOrigen;
		}

		public void setDeptoOrigen(String deptoOrigen) {
			this.deptoOrigen = deptoOrigen;
		}

		public String getDeptoDestino() {
			return deptoDestino;
		}

		public void setDeptoDestino(String deptoDestino) {
			this.deptoDestino = deptoDestino;
		}

		public String getFechaHoraInicio() {
			return fechaHoraInicio;
		}

		public void setFechaHoraInicio(String fechaHoraInicio) {
			this.fechaHoraInicio = fechaHoraInicio;
		}

		public int getNumeroCajas() {
			return numeroCajas;
		}

		public void setNumeroCajas(int numeroCajas) {
			this.numeroCajas = numeroCajas;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

		public Cliente getCliente() {
			return cliente;
		}

		public void setCliente(Cliente cliente) {
			this.cliente = cliente;
		}

		public Repartidor getRepartidor() {
			return repartidor;
		}

		public void setRepartidor(Repartidor repartidor) {
			this.repartidor = repartidor;
		}

		public Vehiculo getVehiculo() {
			return vehiculo;
		}

		public void setVehiculo(Vehiculo vehiculo) {
			this.vehiculo = vehiculo;
		}
	    
	    
}
