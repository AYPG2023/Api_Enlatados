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

		public String getLicencia() {
			return licencia;
		}

		public void setLicencia(String licencia) {
			this.licencia = licencia;
		}

		public String getTelefono() {
			return telefono;
		}

		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}
	    
	    
}
