package com.ap.enlatados.repository;

import com.ap.enlatados.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente,      Long>  {
	
}
