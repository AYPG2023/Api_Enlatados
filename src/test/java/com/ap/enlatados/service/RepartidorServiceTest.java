package com.ap.enlatados.service;

import com.ap.enlatados.entity.Repartidor;
import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.dto.EdgeDTO;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class RepartidorServiceTest {

    private RepartidorService service;

    @Before
    public void setUp() {
        service = new RepartidorService();
    }

    @Test
    public void testCrearYListar() {
        Repartidor r1 = new Repartidor("111", "A", "B", "L1", "NL1", "T1");
        Repartidor r2 = new Repartidor("222", "C", "D", "L2", "NL2", "T2");
        service.crear(r1);
        service.crear(r2);

        List<Repartidor> list = service.listar();
        assertEquals(2, list.size());
        assertEquals("111", list.get(0).getDpi());
        assertEquals("222", list.get(1).getDpi());
    }

    @Test
    public void testDequeue() {
        service.crear(new Repartidor("111", "A","B","L","N","T"));
        service.crear(new Repartidor("222", "C","D","L","N","T"));
        Repartidor first = service.dequeue();
        assertNotNull(first);
        assertEquals("111", first.getDpi());
        // ahora sólo queda el segundo
        List<Repartidor> list = service.listar();
        assertEquals(1, list.size());
        assertEquals("222", list.get(0).getDpi());
    }

    @Test
    public void testBuscarYEliminar() {
        service.crear(new Repartidor("111", "A","B","L","N","T"));
        Repartidor found = service.buscar("111");
        assertEquals("111", found.getDpi());

        service.eliminar("111");
        assertTrue(service.listar().isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testBuscarNoExistenteLanza() {
        service.buscar("999");
    }

    @Test
    public void testModificar() {
        Repartidor original = new Repartidor("111", "A","B","L","N","T");
        service.crear(original);
        Repartidor nuevo = new Repartidor("111", "AA","BB","L2","N2","T2");
        service.modificar("111", nuevo);

        Repartidor found = service.buscar("111");
        assertEquals("AA", found.getNombre());
        assertEquals("BB", found.getApellidos());
    }

    @Test
    public void testCargarRepartidoresDesdeCsv() throws Exception {
        String csv =
                "DPI;Nombre;Apellido;TipoLicencia;NumeroLicencia;Telefono\n" +
                        "111;A;B;L1;NL1;T1\n" +
                        "222;C;D;L2;NL2;T2\n";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        int count = service.cargarRepartidoresDesdeCsv(is);
        assertEquals(2, count);
        List<Repartidor> list = service.listar();
        assertEquals(2, list.size());
        assertEquals("111", list.get(0).getDpi());
        assertEquals("222", list.get(1).getDpi());
    }

    @Test(expected = RepartidorService.BulkLoadException.class)
    public void testCargarRepartidoresConDuplicadoEnCsv() throws Exception {
        String csv =
                "DPI;Nombre;Apellido;TipoLicencia;NumeroLicencia;Telefono\n" +
                        "111;A;B;L1;NL1;T1\n" +
                        "111;X;Y;L2;NL2;T2\n";  // duplicado
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        service.cargarRepartidoresDesdeCsv(is);
    }

    @Test
    public void testObtenerDiagramaRepartidoresDTO() {
        service.crear(new Repartidor("111", "A","B","L","N","T"));
        service.crear(new Repartidor("222", "C","D","L","N","T"));
        DiagramDTO dto = service.obtenerDiagramaRepartidoresDTO();

        List<NodeDTO> nodes = dto.getNodes();
        List<EdgeDTO> edges = dto.getEdges();

        assertEquals(2, nodes.size());
        assertEquals("111", nodes.get(0).getLabel());
        assertEquals("222", nodes.get(1).getLabel());

        assertEquals(1, edges.size());
        assertEquals(0, edges.get(0).getFrom());
        assertEquals(1, edges.get(0).getTo());
    }
}
