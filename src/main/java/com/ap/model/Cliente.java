package com.ap.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Cliente {

	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;              // clave interna auto‑increment

	    @Column(unique = true, nullable = false)
	    private String dpi;           // clave “externa”

	    private String nombre;
	    private String apellidos;
	    private String telefono;
	    private String direccion;
}
