package com.ap.enlatados.service;

import com.ap.enlatados.entity.Cliente;
import com.ap.enlatados.service.ClienteService.BulkLoadException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ClienteServiceTest {

    private ClienteService clienteService;

    @Before
    public void setUp() {
        clienteService = new ClienteService();
    }

    @Test
    public void testCrearBuscarListar() {
        Cliente c1 = new Cliente("111", "A", "B", "123", "D1");
        Cliente c2 = new Cliente("222", "C", "D", "456", "D2");
        clienteService.crear(c1);
        clienteService.crear(c2);

        // Buscar existentes
        assertEquals(c1, clienteService.buscar("111"));
        assertEquals(c2, clienteService.buscar("222"));

        // Listar en orden de clave (111, 222)
        List<Cliente> list = clienteService.listar();
        assertEquals(2, list.size());
        assertEquals("111", list.get(0).getDpi());
        assertEquals("222", list.get(1).getDpi());
    }

    @Test(expected = NoSuchElementException.class)
    public void testBuscarNoExistente() {
        clienteService.buscar("999");
    }

    @Test
    public void testActualizarYEliminar() {
        Cliente original = new Cliente("333", "X", "Y", "000", "D3");
        clienteService.crear(original);

        Cliente updated = new Cliente("333", "MX", "MY", "111", "D3-upd");
        clienteService.actualizar("333", updated);

        Cliente res = clienteService.buscar("333");
        assertEquals("MX", res.getNombre());
        assertEquals("MY", res.getApellidos());

        clienteService.eliminar("333");
        try {
            clienteService.buscar("333");
            fail("Debería lanzar NoSuchElementException tras eliminar");
        } catch (NoSuchElementException ex) {
            // OK
        }
    }

    @Test
    public void testCargarClientesDesdeCsv() throws Exception {
        String csv =
                "dpi;nombre;apellidos;telefono;direccion\n" +
                        "100;Nom;Ape;111;Dir1\n" +
                        "200;Nom2;Ape2;222;Dir2\n";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        int count = clienteService.cargarClientesDesdeCsv(is);
        assertEquals(2, count);

        List<Cliente> loaded = clienteService.listar();
        assertEquals(2, loaded.size());
        assertTrue(loaded.stream().anyMatch(c -> c.getDpi().equals("100")));
        assertTrue(loaded.stream().anyMatch(c -> c.getDpi().equals("200")));
    }

    @Test(expected = BulkLoadException.class)
    public void testCargarCsvDuplicadosEnArchivo() throws Exception {
        String csv =
                "dpi;nombre;apellidos;telefono;direccion\n" +
                        "300;N;A;1;D\n" +
                        "300;N2;A2;2;D2\n";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        clienteService.cargarClientesDesdeCsv(is);
    }
}
