package com.ap.enlatados.model;

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
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getDpi() {
			return dpi;
		}
		public void setDpi(String dpi) {
			this.dpi = dpi;
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
		public String getTelefono() {
			return telefono;
		}
		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}
		public String getDireccion() {
			return direccion;
		}
		public void setDireccion(String direccion) {
			this.direccion = direccion;
		}
	    
	    
}
