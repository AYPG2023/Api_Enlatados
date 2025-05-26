package com.ap.enlatados.service;

import com.ap.enlatados.entity.Usuario;
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

public class UsuarioServiceTest {

    private UsuarioService service;

    @Before
    public void setUp() {
        service = new UsuarioService();
    }

    @Test
    public void testRegistrarYListar() {
        Usuario u = service.registrar(1L, "Juan", "Pérez", "juan@example.com", "pass");
        assertNotNull(u);
        assertEquals(Long.valueOf(1L), u.getId());

        List<Usuario> all = service.listar();
        assertEquals(1, all.size());
        assertSame(u, all.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarDuplicateId() {
        service.registrar(1L, "A", "B", "a@b.com", "pw");
        service.registrar(1L, "C", "D", "c@d.com", "pw2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarDuplicateEmail() {
        service.registrar(1L, "A", "B", "email@x.com", "pw");
        service.registrar(2L, "C", "D", "email@x.com", "pw2");
    }

    @Test
    public void testBuscarPorIdYEmail() {
        service.registrar(5L, "Ana", "García", "ana@domain.com", "pwd");
        Usuario byId = service.buscarPorId(5L);
        assertEquals("ana@domain.com", byId.getEmail());

        Usuario byEmail = service.buscarPorEmail("ana@domain.com");
        assertEquals(Long.valueOf(5L), byEmail.getId());
    }

    @Test(expected = NoSuchElementException.class)
    public void testBuscarPorIdNotFound() {
        service.buscarPorId(99L);
    }

    @Test(expected = NoSuchElementException.class)
    public void testBuscarPorEmailNotFound() {
        service.buscarPorEmail("no@existe.com");
    }

    @Test
    public void testActualizar() {
        service.registrar(1L, "Old", "Name", "old@e.com", "pw");
        Usuario updated = service.actualizar(1L, "New", "Name", "new@e.com", "newpw");
        assertEquals("New", updated.getNombre());
        assertEquals("Name", updated.getApellidos());
        assertEquals("new@e.com", updated.getEmail());
        assertEquals("newpw", updated.getPassword());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActualizarDuplicateEmail() {
        service.registrar(1L, "A", "B", "a@b.com", "pw");
        service.registrar(2L, "C", "D", "c@d.com", "pw2");
        // intentar actualizar el usuario 1 con el email del 2
        service.actualizar(1L, "X", "Y", "c@d.com", "pw");
    }

    @Test
    public void testIniciarSesionYPerfilYCerrar() {
        service.registrar(1L, "User", "One", "u1@u.com", "secret");
        Usuario logged = service.iniciarSesion("u1@u.com", "secret");
        assertNotNull(logged);
        assertEquals(Long.valueOf(1L), service.obtenerPerfilActual().getId());

        service.cerrarSesion();
        // tras cerrar, obtenerPerfilActual debe fallar
        try {
            service.obtenerPerfilActual();
            fail("Se esperaba NoSuchElementException tras cerrar sesión");
        } catch (NoSuchElementException e) {
            // ok
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testIniciarSesionInvalidCredentials() {
        service.iniciarSesion("no@existe.com", "nopw");
    }

    @Test(expected = NoSuchElementException.class)
    public void testObtenerPerfilSinLogin() {
        service.obtenerPerfilActual();
    }

    @Test
    public void testEliminar() {
        service.registrar(2L, "X", "Y", "xy@z.com", "pw");
        service.eliminar(2L);
        assertTrue(service.listar().isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEliminarNoEncontrado() {
        service.eliminar(42L);
    }

    @Test
    public void testCargarUsuariosDesdeCsv() throws Exception {
        String csv = "Id;Nombre;Apellido;Email;Contraseña\n"
                + "0;A;B;a@b.com;pw1\n"
                + "1;C;D;c@d.com;pw2\n";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        int count = service.cargarUsuariosDesdeCsv(is);
        assertEquals(2, count);
        assertEquals(2, service.listar().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCargarUsuariosDesdeCsvDuplicate() throws Exception {
        String csv = "Id;Nombre;Apellido;Email;Contraseña\n"
                + "0;A;B;a@b.com;pw1\n"
                + "0;E;F;ef@f.com;pw2\n";
        InputStream is = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        service.cargarUsuariosDesdeCsv(is);
    }

    @Test
    public void testObtenerDiagramaUsuariosDTO() {
        // Usamos IDs 0,1,2 para que los edges salgan correctamente
        service.registrar(0L, "A", "B", "a@b.com", "pw1");
        service.registrar(1L, "C", "D", "c@d.com", "pw2");
        service.registrar(2L, "E", "F", "e@f.com", "pw3");

        DiagramDTO dto = service.obtenerDiagramaUsuariosDTO();
        List<NodeDTO> nodes = dto.getNodes();
        List<EdgeDTO> edges = dto.getEdges();

        assertEquals(3, nodes.size());
        assertEquals(2, edges.size());

        for (int i = 0; i < 3; i++) {
            assertEquals(i, nodes.get(i).getId());
            assertEquals(String.valueOf(i), nodes.get(i).getLabel());
        }

        // edge 0→1 y 1→2
        assertEquals(0, edges.get(0).getFrom());
        assertEquals(1, edges.get(0).getTo());
        assertEquals(1, edges.get(1).getFrom());
        assertEquals(2, edges.get(1).getTo());
    }
}
