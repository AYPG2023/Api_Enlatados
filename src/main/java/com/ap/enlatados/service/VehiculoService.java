package com.ap.enlatados.service;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.entity.Vehiculo;
import com.ap.enlatados.service.eddlineales.Cola;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehiculoService {

    private static final Map<String,List<String>> LICENCIA_COMPAT = Map.of(
            "M",  List.of("MOTO"),
            "P",  List.of("CARRO"),
            "A",  List.of("CARRO","BUS_URBANO","BUS_EXTRAURBANO"),
            "C",  List.of("CARRO","REMOLQUE"),
            "CD", List.of("CARRO","REMOLQUE"),
            "CC", List.of("CARRO","REMOLQUE"),
            "MI", List.of("CARRO","MOTO"),
            "O",  List.of("CARRO","BUS_URBANO"),
            "E",  List.of("MAQUINARIA")
    );

    private final Cola<Vehiculo> queue = new Cola<>();

    /**
     * Crea un nuevo vehículo en la cola.
     * Lanza 400 Bad Request si la placa ya existe.
     */
    public void crear(Vehiculo v) {
        // Validación de duplicados
        boolean exists = queue.toList().stream()
                .anyMatch(existing -> existing.getPlaca().equalsIgnoreCase(v.getPlaca()));
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La placa '" + v.getPlaca() + "' ya existe. Por favor usa otra placa.");
        }
        queue.enqueue(v);
    }

    public Vehiculo dequeue() {
        return queue.dequeue();
    }

    public void reenqueue(Vehiculo v) {
        crear(v);
    }

    public Vehiculo buscar(String placa) {
        return queue.toList().stream()
                .filter(v -> v.getPlaca().equals(placa))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vehículo no encontrado o no disponible: " + placa));
    }

    public void eliminar(String placa) {
        queue.removeIf(v -> v.getPlaca().equals(placa));
    }

    public void modificar(String placa, Vehiculo nuevo) {
        eliminar(placa);
        crear(nuevo);
    }

    public List<Vehiculo> listar() {
        return queue.toList();
    }

    public List<Vehiculo> listarPorTipo(String tipoVehiculo) {
        return listar().stream()
                .filter(v -> v.getTipoVehiculo().equals(tipoVehiculo))
                .collect(Collectors.toList());
    }

    public List<Vehiculo> listarPorLicencia(String tipoLicencia) {
        List<String> permitidos = LICENCIA_COMPAT.getOrDefault(tipoLicencia, Collections.emptyList());
        return listar().stream()
                .filter(v -> permitidos.contains(v.getTipoVehiculo()))
                .collect(Collectors.toList());
    }

    /**
     * Carga masivo desde CSV con cabecera:
     * Placa;Marca;Modelo;Color;año;Tipo de transmisión;TipoVehiculo
     */
    public int cargarMasivo(InputStream is) throws IOException {
        CSVFormat fmt = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withDelimiter(';')
                .withIgnoreEmptyLines()
                .withTrim();

        // 1) Obtengo el set de placas ya en la cola
        Set<String> exist = queue.toList().stream()
                .map(v -> v.getPlaca().toUpperCase())
                .collect(Collectors.toSet());

        // 2) Guardaré aquí los duplicados y los nuevos
        Set<String> duplicadas = new LinkedHashSet<>();
        List<Vehiculo> pendientes = new ArrayList<>();

        try (
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, fmt)
        ) {
            for (CSVRecord r : parser) {
                String placa = r.get("Placa").trim();
                String clave = placa.toUpperCase();

                if (exist.contains(clave)) {
                    // ya existía en la cola
                    duplicadas.add(placa);
                } else if (pendientes.stream()
                        .map(v -> v.getPlaca().toUpperCase())
                        .anyMatch(p -> p.equals(clave))) {
                    // ya apareció antes en este mismo CSV
                    duplicadas.add(placa);
                } else {
                    // construct y guardo para encolar luego
                    Vehiculo v = new Vehiculo(
                            placa,
                            r.get("Marca"),
                            r.get("Modelo"),
                            r.get("Color"),
                            Integer.parseInt(r.get("año")),
                            r.get("Tipotransmisión"),
                            r.get("TipoVehiculo")
                    );
                    pendientes.add(v);
                    exist.add(clave);
                }
            }
        }

        // 3) Si encontré duplicados, corto y devuelvo error
        if (!duplicadas.isEmpty()) {
            String msg = "Las siguientes placas ya existen o están repetidas: "
                    + String.join(", ", duplicadas);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }

        // 4) Si todo esta bien, encolo y retorno el conteo
        pendientes.forEach(queue::enqueue);
        return pendientes.size();
    }


    public DiagramDTO obtenerDiagramaColaDTO() {
        List<Vehiculo> vs = listar();
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();
        for (int i = 0; i < vs.size(); i++) {
            nodes.add(new NodeDTO(i, vs.get(i).getPlaca()));
            if (i < vs.size() - 1) {
                edges.add(new EdgeDTO(i, i + 1));
            }
        }
        return new DiagramDTO(nodes, edges);
    }
}
