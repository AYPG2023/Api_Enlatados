package com.ap.model;

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
}
