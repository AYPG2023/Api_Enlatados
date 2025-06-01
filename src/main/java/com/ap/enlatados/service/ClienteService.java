package com.ap.enlatados.service;

import com.ap.enlatados.entity.Cliente;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.service.eddNoLineales.ArbolAvl;
import com.ap.enlatados.service.eddNoLineales.AvlNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ClienteService {
	
	    public static class BulkLoadException extends RuntimeException {
	        public BulkLoadException(String msg) { super(msg); }
	    }

    // 1) Sustituimos el árbol interno por tu ArbolAvl genérico:
    private final ArbolAvl<Cliente, String> tree = new ArbolAvl<>(Cliente::getDpi);

    public void crear(Cliente c) {
        tree.insert(c);
    }

    public Cliente buscar(String dpi) {
        Cliente c = tree.find(dpi);
        if (c == null) throw new NoSuchElementException("Cliente no encontrado: " + dpi);
        return c;
    }

    public List<Cliente> listar() {
        return tree.inOrder();
    }

    public void actualizar(String dpi, Cliente nuevo) {
        tree.delete(dpi);
        tree.insert(nuevo);
    }

    public void eliminar(String dpi) {
        tree.delete(dpi);
    }



    /** 2) Metodo para cargar archivos csv */
    public int cargarClientesDesdeCsv(InputStream is) throws IOException {
        CSVFormat fmt = CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withDelimiter(';')
            .withIgnoreEmptyLines()
            .withTrim();

        // Ya existentes en el árbol
        Set<String> existentes = listar().stream()
            .map(Cliente::getDpi)
            .collect(Collectors.toSet());

        Set<String> enArchivo = new HashSet<>();
        List<Cliente> nuevos = new ArrayList<>();
        List<String> errores = new ArrayList<>();

        try (
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, fmt)
        ) {
            for (CSVRecord r : parser) {
                String dpi       = r.get("dpi");
                String nombre    = r.get("nombre");
                String apellidos = r.get("apellidos");
                String telefono  = r.get("telefono");
                String direccion = r.get("direccion");

                if (dpi.isBlank() || nombre.isBlank() || apellidos.isBlank()
                        || telefono.isBlank() || direccion.isBlank()) {
                    errores.add(dpi + " (campos vacíos)");
                    continue;
                }
                if (existentes.contains(dpi)) {
                    errores.add(dpi + " (ya existe)");
                    continue;
                }
                if (!enArchivo.add(dpi)) {
                    errores.add(dpi + " (duplicado en archivo)");
                    continue;
                }
                nuevos.add(new Cliente(dpi, nombre, apellidos, telefono, direccion));
            }
        }

        if (!errores.isEmpty()) {
            throw new BulkLoadException(
                "DPIs inválidos o duplicados: " + String.join(", ", errores)
            );
        }

        // Insertamos en el árbol
        nuevos.forEach(this::crear);
        return nuevos.size();
    }

    /** 3) Diagrama: adaptamos buildDiagram para usar AvlNode */
    public DiagramDTO obtenerDiagramaClientesDTO() {
        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        // Necesitas exponer root -> asumo getRoot()
        AvlNode<Cliente,String> root = tree.getRoot();
        buildDiagram(root, nodes, edges, counter);

        return new DiagramDTO(nodes, edges);
    }

    private int buildDiagram(AvlNode<Cliente,String> node,
                             List<NodeDTO> nodes,
                             List<EdgeDTO> edges,
                             AtomicInteger counter) {
        if (node == null) return -1;
        int id = counter.getAndIncrement();
        nodes.add(new NodeDTO(id, node.data.getDpi()));

        int leftId = buildDiagram(node.left, nodes, edges, counter);
        if (leftId != -1) edges.add(new EdgeDTO(id, leftId));

        int rightId = buildDiagram(node.right, nodes, edges, counter);
        if (rightId != -1) edges.add(new EdgeDTO(id, rightId));

        return id;
    }

    private void diagramaAVL(AvlNode<Cliente,String> node, StringBuilder sb, int nivel) {
        if (node == null) return;
        diagramaAVL(node.right, sb, nivel + 1);
        sb.append("   ".repeat(nivel))
          .append(node.data.getDpi())
          .append(" (").append(node.data.getDireccion()).append(")")
          .append("\n");
        diagramaAVL(node.left, sb, nivel + 1);
    }
}
