package com.ap.enlatados.service;

import com.ap.enlatados.dto.DiagramDTO;
import com.ap.enlatados.dto.NodeDTO;
import com.ap.enlatados.dto.EdgeDTO;
import com.ap.enlatados.entity.Vehiculo;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class VehiculoServiceTest {

    private VehiculoService service;

    @Before
    public void setUp() {
        service = new VehiculoService();
    }

    @Test
    public void testCrearYListar() {
        Vehiculo v = new Vehiculo("ABC123", "Toyota", "Corolla", "Rojo", 2020, "Manual", "CARRO");
        service.crear(v);
        List<Vehiculo> all = service.listar();
        assertEquals(1, all.size());
        assertSame(v, all.get(0));
    }

    @Test
    public void testDequeue() {
        Vehiculo v1 = new Vehiculo("A1", "Mitsubishi", "Lancer", "Blanco", 2018, "Automático", "CARRO");
        Vehiculo v2 = new Vehiculo("B2", "Honda", "Civic", "Negro", 2019, "Manual", "CARRO");
        service.crear(v1);
        service.crear(v2);

        Vehiculo out = service.dequeue();
        assertSame(v1, out);
        List<Vehiculo> remaining = service.listar();
        assertEquals(1, remaining.size());
        assertSame(v2, remaining.get(0));
    }

    @Test
    public void testReenqueue() {
        Vehiculo v = new Vehiculo("XYZ", "Yamaha", "R1", "Azul", 2021, "Integrado", "MOTO");
        service.crear(v);
        Vehiculo d = service.dequeue();
        assertSame(v, d);
        assertTrue(service.listar().isEmpty());

        service.reenqueue(v);
        List<Vehiculo> all = service.listar();
        assertEquals(1, all.size());
        assertSame(v, all.get(0));
    }

    @Test
    public void testBuscarYEliminar() {
        Vehiculo v = new Vehiculo("AAA111", "Ford", "F-150", "Gris", 2022, "4x4", "CARRO");
        service.crear(v);
        Vehiculo found = service.buscar("AAA111");
        assertSame(v, found);

        service.eliminar("AAA111");
        assertTrue(service.listar().isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testBuscarNoExistente() {
        service.buscar("NOEXISTE");
    }

    @Test
    public void testModificar() {
        Vehiculo oldV = new Vehiculo("OLD1", "Nissan", "Sentra", "Blanco", 2015, "Manual", "CARRO");
        service.crear(oldV);
        Vehiculo newV = new Vehiculo("NEW1", "Nissan", "Sentra", "Azul", 2016, "Automático", "CARRO");
        service.modificar("OLD1", newV);

        // OLD1 ya no debe existir
        try {
            service.buscar("OLD1");
            fail("Se esperaba NoSuchElementException para OLD1");
        } catch (NoSuchElementException e) {
            // ok
        }

        // NEW1 sí debe existir
        Vehiculo fetched = service.buscar("NEW1");
        assertSame(newV, fetched);
    }

    @Test
    public void testListarPorTipo() {
        Vehiculo v1 = new Vehiculo("T1", "Marca", "Mod1", "Color", 2000, "Tipo", "MOTO");
        Vehiculo v2 = new Vehiculo("T2", "Marca", "Mod2", "Color", 2001, "Tipo", "CARRO");
        service.crear(v1);
        service.crear(v2);

        List<Vehiculo> motos = service.listarPorTipo("MOTO");
        assertEquals(1, motos.size());
        assertSame(v1, motos.get(0));
    }

    @Test
    public void testListarPorLicencia() {
        // Según LICENCIA_COMPAT, "M" permite "MOTO"
        Vehiculo m = new Vehiculo("MOTO1", "Yamaha", "MT-07", "Negro", 2020, "Manual", "MOTO");
        Vehiculo c = new Vehiculo("CAR123", "Toyota", "Corolla", "Blanco", 2021, "Auto", "CARRO");
        service.crear(m);
        service.crear(c);

        List<Vehiculo> licM = service.listarPorLicencia("M");
        assertEquals(1, licM.size());
        assertSame(m, licM.get(0));

        // "A" permite CARRO y buses
        List<Vehiculo> licA = service.listarPorLicencia("A");
        assertTrue(licA.contains(c));
    }

    @Test
    public void testCargarMasivo() throws Exception {
        String csv =
                "Placa;Marca;Modelo;Color;año;Tipo de transmisión;TipoVehiculo\n" +
                        "P1;Mazda;3;Rojo;2017;Manual;CARRO\n" +
                        "P2;Kawasaki;Ninja;Verde;2019;Integrado;MOTO\n";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        int count = service.cargarMasivo(is);
        assertEquals(2, count);

        assertEquals(2, service.listar().size());
        assertNotNull(service.buscar("P1"));
        assertNotNull(service.buscar("P2"));
    }

    @Test
    public void testObtenerDiagramaColaDTO() {
        Vehiculo v1 = new Vehiculo("X1", "X", "X", "X", 2000, "T", "CARRO");
        Vehiculo v2 = new Vehiculo("X2", "X", "X", "X", 2001, "T", "CARRO");
        Vehiculo v3 = new Vehiculo("X3", "X", "X", "X", 2002, "T", "CARRO");
        service.crear(v1);
        service.crear(v2);
        service.crear(v3);

        DiagramDTO dto = service.obtenerDiagramaColaDTO();
        List<NodeDTO> nodes = dto.getNodes();
        List<EdgeDTO> edges = dto.getEdges();

        assertEquals(3, nodes.size());
        assertEquals(2, edges.size());

        for (int i = 0; i < 3; i++) {
            assertEquals(i, nodes.get(i).getId());
            assertEquals("X" + (i+1), nodes.get(i).getLabel());
        }

        // Comprueba las aristas 0→1, 1→2
        assertEquals(0, edges.get(0).getFrom());
        assertEquals(1, edges.get(0).getTo());
        assertEquals(1, edges.get(1).getFrom());
        assertEquals(2, edges.get(1).getTo());
    }
}
