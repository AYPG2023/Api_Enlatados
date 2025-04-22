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
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getPlaca() {
			return placa;
		}
		public void setPlaca(String placa) {
			this.placa = placa;
		}
		public String getMarca() {
			return marca;
		}
		public void setMarca(String marca) {
			this.marca = marca;
		}
		public String getModelo() {
			return modelo;
		}
		public void setModelo(String modelo) {
			this.modelo = modelo;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public int getAnio() {
			return anio;
		}
		public void setAnio(int anio) {
			this.anio = anio;
		}
		public String getTransmision() {
			return transmision;
		}
		public void setTransmision(String transmision) {
			this.transmision = transmision;
		}
}
