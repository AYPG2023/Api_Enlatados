package com.ap.enlatados.service;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.dto.ResumenDTO;
import com.ap.enlatados.entity.Caja;
import com.ap.enlatados.service.eddlineales.Pila;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class CajaService {

    // Map producto → pila (LIFO) de cajas
    private final Map<String, Pila<Caja>> inventario = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * Agrega (push) 'cantidad' cajas nuevas para el producto dado.
     * @return la lista de cajas creadas.
     */
    public List<Caja> agregarCajas(String producto, int cantidad) {
        Pila<Caja> pila = inventario.computeIfAbsent(producto, k -> new Pila<>());
        List<Caja> creadas = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad; i++) {
            Caja c = new Caja(nextId.getAndIncrement(), producto, LocalDateTime.now().toString());
            pila.push(c);
            creadas.add(c);
        }
        return creadas;
    }
    
    /** Reencola una caja específica (al completar/eliminar pedido) */
    public void reencolarCaja(String producto, long idCaja, String fechaIngreso) {
        Pila<Caja> pila = inventario.computeIfAbsent(producto, k -> new Pila<>());
        pila.push(new Caja(idCaja, producto, fechaIngreso));
        nextId.updateAndGet(n -> Math.max(n, idCaja + 1));
    }

    /**
     * Extrae (pop) hasta 'cantidad' cajas del tope de la pila del producto.
     * @return lista de cajas extraídas (vacía si no hay ninguna).
     */
    public List<Caja> extraerCajas(String producto, int cantidad) {
        Pila<Caja> pila = inventario.getOrDefault(producto, new Pila<>());
        List<Caja> sacadas = new ArrayList<>(cantidad);
        for (int i = 0; i < cantidad && !pila.isEmpty(); i++) {
            sacadas.add(pila.pop());
        }
        return sacadas;
    }

    /**
     * Lista todas las cajas de un producto en orden LIFO (tope primero).
     */
    public List<Caja> listarCajas(String producto) {
        Pila<Caja> pila = inventario.getOrDefault(producto, new Pila<>());
        return pila.toList();
    }

    /**
     * Carga masivo de cajas desde CSV, cada línea solo lleva el ID.
     * @return número de cajas cargadas.
     */
    public int cargarDesdeCsv(List<String[]> registros) {
        int totalCreadas = 0;

        for (String[] linea : registros) {
            // Se espera exactamente 2 columnas: [0]=producto, [1]=cantidad
            if (linea.length != 2) {
                continue; // omite líneas mal formateadas
            }
            String producto = linea[0].trim();
            int cantidad;
            try {
                cantidad = Integer.parseInt(linea[1].trim());
            } catch (NumberFormatException e) {
                continue; // omite si la cantidad no es numérica
            }
            // Para cada producto, agregamos 'cantidad' cajas
            Pila<Caja> pila = inventario.computeIfAbsent(producto, k -> new Pila<>());
            for (int i = 0; i < cantidad; i++) {
                long id = nextId.getAndIncrement();
                pila.push(new Caja(id, producto, LocalDateTime.now().toString()));
                totalCreadas++;
            }
        }

        return totalCreadas;
    }


    /**
     * Genera un diagrama textual de la pila"
     */
    public DiagramDTO obtenerDiagramaProductosDTO() {
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();

        List<String> productos = new ArrayList<>(inventario.keySet());
        Collections.sort(productos);

        for (int i = 0; i < productos.size(); i++) {
            nodes.add(new NodeDTO(i, productos.get(i)));
            if (i + 1 < productos.size()) {
                edges.add(new EdgeDTO(i, i + 1));
            }
        }

        return new DiagramDTO(nodes, edges);
    }

    /**
     * Devuelve un listado de ResumenDTO con un resumen por producto:
     *  - cantidad de cajas (tamaño de la pila)
     */
    public List<ResumenDTO> obtenerResumenDeProductos() {
        return inventario.entrySet().stream()
            .map(entry -> {
                String producto = entry.getKey();
                Pila<Caja> pila = entry.getValue();
                long cantidad = pila.toList().size();
                String fechaUltima = !pila.isEmpty()
                    ? pila.peek().getFechaIngreso()
                    : "";
                return new ResumenDTO(producto, cantidad, fechaUltima);
            })
            .collect(Collectors.toList());
    }
}
