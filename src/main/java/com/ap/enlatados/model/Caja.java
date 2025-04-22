package com.ap.enlatados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Caja {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;               // correlativo auto‑increment
    private String fechaIngreso;   // ISO‑date (o LocalDateTime)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFechaIngreso() {
		return fechaIngreso;
	}
	public void setFechaIngreso(String fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}
}
