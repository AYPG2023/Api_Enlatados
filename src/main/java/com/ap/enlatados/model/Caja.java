package com.ap.enlatados.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Caja {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;               // correlativo auto‑increment
    private String fechaIngreso;   // ISO‑date (o LocalDateTime)

}
