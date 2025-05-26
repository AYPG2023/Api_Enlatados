package com.ap.enlatados.service;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.entity.Vehiculo;
import com.ap.enlatados.service.eddlineales.Cola;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

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

    public void crear(Vehiculo v) {
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
            .orElseThrow(() -> new NoSuchElementException(
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
        List<String> permitidos = LICENCIA_COMPAT
            .getOrDefault(tipoLicencia, Collections.emptyList());
        return listar().stream()
            .filter(v -> permitidos.contains(v.getTipoVehiculo()))
            .collect(Collectors.toList());
    }

    /**  
     * Carga masiva desde CSV con cabecera:
     * Placa;Marca;Modelo;Color;año;Tipo de transmisión;TipoVehiculo  
     */
    public int cargarMasivo(InputStream is) throws IOException {
        CSVFormat fmt = CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(';')
            .withIgnoreEmptyLines()
            .withTrim();

        try (
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, fmt)
        ) {
            int count = 0;
            for (CSVRecord r : parser) {
                String placa        = r.get("Placa");
                String marca        = r.get("Marca");
                String modelo       = r.get("Modelo");
                String color        = r.get("Color");
                int    anio         = Integer.parseInt(r.get("año"));
                String transmision  = r.get("Tipo de transmisión");
                String tipoVehiculo = r.get("TipoVehiculo");
                crear(new Vehiculo(
                    placa, marca, modelo, color, anio, transmision, tipoVehiculo
                ));
                count++;
            }
            return count;
        }
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
