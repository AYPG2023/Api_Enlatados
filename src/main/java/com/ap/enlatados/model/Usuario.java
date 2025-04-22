package com.ap.enlatados.model;

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

	    @Column(nullable = false, unique = true)
	    private String email;

	    
	    private String apellidos;

	    @Column(nullable = false)
	    private String password;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getApellidos() {
			return apellidos;
		}

		public void setApellidos(String apellidos) {
			this.apellidos = apellidos;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		
		
	    
	    
}
