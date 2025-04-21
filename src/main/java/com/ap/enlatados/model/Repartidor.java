package com.ap.enlatados.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Repartidor {

	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true, nullable = false)
	    private String dpi;

	    private String nombre;
	    private String apellidos;

	    @Column(length = 1)           // A | B | C
	    private String licencia;

	    private String telefono;
}
