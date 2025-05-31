package com.ap.enlatados.service;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.entity.Repartidor;
import com.ap.enlatados.service.eddlineales.Cola;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RepartidorService {

    private final Cola<Repartidor> cola = new Cola<>();

    /** Encola un repartidor nuevo */
    public void crear(Repartidor r) {
        cola.enqueue(r);
    }

    /** Alias de crear */
    public void enqueue(Repartidor r) {
        crear(r);
    }

    /** Desencola (asigna) el siguiente repartidor o null si no hay */
    public Repartidor dequeue() {
        return cola.dequeue();
    }

    /** Reencola (libera) un repartidor */
    public void reenqueue(Repartidor r) {
        cola.enqueue(r);
    }

    /** Busca sin extraer: lanza NoSuchElementException si no existe */
    public Repartidor buscar(String dpi) {
        return cola.toList().stream()
            .filter(r -> r.getDpi().equals(dpi))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Repartidor no encontrado: " + dpi));
    }

    /** Elimina de la cola por DPI */
    public void eliminar(String dpi) {
        cola.removeIf(r -> r.getDpi().equals(dpi));
    }

    /** Modifica (elimínalo y vuelve a encolar con datos nuevos) */
    public void modificar(String dpi, Repartidor nuevo) {
        eliminar(dpi);
        crear(nuevo);
    }

    /** Lista todos los repartidores actualmente en cola */
    public List<Repartidor> listar() {
        return cola.toList();
    }

    /** Excepción para errores de carga masiva */
    public static class BulkLoadException extends RuntimeException {
        public BulkLoadException(String msg) { super(msg); }
    }

    /**
     * Carga repartidores desde CSV con header:
     * DPI;Nombre;Apellido;TipoLicencia;NumeroLicencia;Telefono
     * @return cantidad de registros importados
     */
    public int cargarRepartidoresDesdeCsv(InputStream is)
            throws IOException, BulkLoadException {

        CSVFormat fmt = CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(';')
            .withIgnoreEmptyLines()
            .withTrim();

        // DPI ya en cola
        Set<String> existentes = new HashSet<>();
        listar().forEach(r -> existentes.add(r.getDpi()));

        Set<String> vistosEnArchivo = new HashSet<>();
        List<Repartidor> nuevos = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        try (
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, fmt)
        ) {
            for (CSVRecord rec : parser) {
                String dpi        = rec.get("DPI");
                String nombre     = rec.get("Nombre");
                String apellido   = rec.get("Apellido");
                String tipoLic    = rec.get("TipoLicencia");
                String numLic     = rec.get("NumeroLicencia");
                String telefono   = rec.get("Telefono");

                if (dpi.isBlank() || nombre.isBlank() || apellido.isBlank()
                 || tipoLic.isBlank() || numLic.isBlank() || telefono.isBlank()) {
                    errores.add(dpi + " (incompleto)");
                    continue;
                }
                if (existentes.contains(dpi)) {
                    errores.add(dpi + " (ya existe)");
                }
                else if (!vistosEnArchivo.add(dpi)) {
                    errores.add(dpi + " (duplicado en CSV)");
                }
                else {
                    nuevos.add(new Repartidor(dpi, nombre, apellido, tipoLic, numLic, telefono));
                }
            }
        }

        if (!errores.isEmpty()) {
            throw new BulkLoadException("Errores en CSV: " + String.join(", ", errores));
        }

        nuevos.forEach(this::crear);
        return nuevos.size();
    }

    /** Genera un DiagramDTO de la cola actual */
    public DiagramDTO obtenerDiagramaRepartidoresDTO() {
        List<Repartidor> list = listar();
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            nodes.add(new NodeDTO(i, list.get(i).getDpi()));
            if (i < list.size() - 1) {
                edges.add(new EdgeDTO(i, i + 1));
            }
        }
        return new DiagramDTO(nodes, edges);
    }
}
