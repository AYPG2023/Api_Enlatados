package com.ap.enlatados.service;

import com.ap.enlatados.model.Caja;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Service
public class CajaService {
    private Stack<Caja> pilaCajas = new Stack<>();
    private Long nextId = 1L;

    /** Agregar una nueva caja (push) */
    public Caja agregarCaja() {
        Caja nueva = new Caja(nextId++);
        pilaCajas.push(nueva);
        return nueva;
    }

    /** Extraer la caja superior (pop) */
    public Caja extraerCaja() {
        if (pilaCajas.isEmpty()) {
            return null;
        }
        return pilaCajas.pop();
    }

    /** Listar todas las cajas (sin modificar pila) */
    public List<Caja> listarCajas() {
        return new ArrayList<>(pilaCajas);
    }

    /** Cargar desde CSV (ID manual) */
    public int cargarDesdeCsv(List<String[]> registros) {
        int contador = 0;
        for (String[] linea : registros) {
            if (linea.length != 1) continue;
            Long id = Long.parseLong(linea[0].trim());
            pilaCajas.push(new Caja(id));
            if (id >= nextId) nextId = id + 1;
            contador++;
        }
        return contador;
    }

    /** Generar diagrama de la pila estilo texto */
    public String obtenerDiagramaPila() {
        StringBuilder sb = new StringBuilder();
        for (int i = pilaCajas.size() - 1; i >= 0; i--) {
            Caja c = pilaCajas.get(i);
            sb.append("[").append(c.getId()).append("] -> ");
        }
        sb.append("NULL");
        return sb.toString();
    }
}
