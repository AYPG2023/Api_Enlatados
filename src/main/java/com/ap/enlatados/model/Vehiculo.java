package com.ap.enlatados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Vehiculo {
	
	
	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true, nullable = false)
	    private String placa;

	    private String marca;
	    private String modelo;
	    private String color;
	    private int   anio;
	    private String transmision;

}
