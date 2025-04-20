package com.ap.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)   // auto‑increment (1,2,3…)
	    private Long id;

	    @Column(nullable = false)
	    private String nombre;

	    private String apellidos;

	    @Column(nullable = false)
	    private String password;
}
