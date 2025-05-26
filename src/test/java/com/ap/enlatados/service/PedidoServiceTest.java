package com.ap.enlatados.service;

import com.ap.enlatados.dto.PedidoDTO;
import com.ap.enlatados.dto.PedidoItemDTO;
import com.ap.enlatados.entity.Caja;
import com.ap.enlatados.entity.CajaPedido;
import com.ap.enlatados.entity.Cliente;
import com.ap.enlatados.entity.Pedido;
import com.ap.enlatados.entity.Repartidor;
import com.ap.enlatados.entity.Vehiculo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PedidoServiceTest {

    @Mock private CajaService cajaService;
    @Mock private ClienteService clienteService;
    @Mock private RepartidorService repartidorService;
    @Mock private VehiculoService vehiculoService;

    @InjectMocks private PedidoService service;

    @Before
    public void setUp() {
        // Con @RunWith(MockitoJUnitRunner.class) no es necesario initMocks()
    }

    @Test
    public void testCrearPedidoManualExitoso() {
        // --- Stubs ---
        Cliente cliente = mock(Cliente.class);
        when(clienteService.buscar("dpi1")).thenReturn(cliente);

        Caja c1 = mock(Caja.class);
        when(c1.getId()).thenReturn(100L);
        when(c1.getProducto()).thenReturn("prod");
        when(c1.getFechaIngreso()).thenReturn(null);
        Caja c2 = mock(Caja.class);
        when(c2.getId()).thenReturn(101L);
        when(c2.getProducto()).thenReturn("prod");
        when(c2.getFechaIngreso()).thenReturn(null);
        when(cajaService.extraerCajas("prod", 2)).thenReturn(Arrays.asList(c1, c2));

        Repartidor rep = mock(Repartidor.class);
        when(repartidorService.buscar("r1")).thenReturn(rep);
        Vehiculo veh = mock(Vehiculo.class);
        when(vehiculoService.buscar("v1")).thenReturn(veh);

        // --- DTO ---
        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpi1");
        dto.setItems(Arrays.asList(new PedidoItemDTO("prod", 2)));
        dto.setTipoAsignacion("MANUAL");
        dto.setRepartidorDpi("r1");
        dto.setVehiculoPlaca("v1");

        // --- Ejecutar ---
        Pedido p = service.crearPedido(dto);

        // --- Verificaciones ---
        assertSame(cliente, p.getCliente());
        assertSame(rep, p.getRepartidor());
        assertSame(veh, p.getVehiculo());
        assertEquals("EnCurso", p.getEstado());
        assertEquals(2, p.getCajas().size());
        // cada CajaPedido conserva el id de la Caja original
        List<CajaPedido> cajas = p.getCajas();
        assertEquals(Long.valueOf(100L), cajas.get(0).getId());
        assertEquals(Long.valueOf(101L), cajas.get(1).getId());

        verify(repartidorService).eliminar("r1");
        verify(vehiculoService).eliminar("v1");
    }

    @Test
    public void testCrearPedidoAutoConRecursos() {
        // Cliente OK
        Cliente cliente = mock(Cliente.class);
        when(clienteService.buscar("dpiX")).thenReturn(cliente);
        // Sin ítems => lista vacía de cajas
        when(cajaService.extraerCajas(anyString(), anyInt())).thenReturn(Collections.singletonList(mock(Caja.class)));

        // AUTO asigna rep y veh por dequeue()
        Repartidor rep = mock(Repartidor.class);
        Vehiculo veh = mock(Vehiculo.class);
        when(repartidorService.dequeue()).thenReturn(rep);
        when(vehiculoService.dequeue()).thenReturn(veh);

        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpiX");
        dto.setItems(Arrays.asList(new PedidoItemDTO("p", 1)));
        dto.setTipoAsignacion("AUTO");

        Pedido p = service.crearPedido(dto);
        assertSame(rep, p.getRepartidor());
        assertSame(veh, p.getVehiculo());
        assertEquals("EnCurso", p.getEstado());
    }

    @Test
    public void testCrearPedidoAutoPendienteSiFaltaVehiculo() {
        Cliente cliente = mock(Cliente.class);
        when(clienteService.buscar("dpiX")).thenReturn(cliente);
        when(cajaService.extraerCajas(anyString(), anyInt())).thenReturn(Collections.singletonList(mock(Caja.class)));

        Repartidor rep = mock(Repartidor.class);
        when(repartidorService.dequeue()).thenReturn(rep);
        when(vehiculoService.dequeue()).thenReturn(null);

        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpiX");
        dto.setItems(Arrays.asList(new PedidoItemDTO("p", 1)));
        dto.setTipoAsignacion("AUTO");

        Pedido p = service.crearPedido(dto);
        assertSame(rep, p.getRepartidor());
        assertNull(p.getVehiculo());
        assertEquals("Pendiente", p.getEstado());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPedidoSinCliente() {
        when(clienteService.buscar("bad")).thenReturn(null);
        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("bad");
        service.crearPedido(dto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPedidoSinStock() {
        Cliente cliente = mock(Cliente.class);
        when(clienteService.buscar("dpi")).thenReturn(cliente);
        when(cajaService.extraerCajas("x", 5)).thenReturn(Collections.emptyList());

        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpi");
        dto.setItems(Arrays.asList(new PedidoItemDTO("x", 5)));
        service.crearPedido(dto);
    }

    @Test
    public void testAsignarRecursosAutomatico() {
        // Crear pedido pendiente (vehículo faltante)
        when(clienteService.buscar("dpi")).thenReturn(mock(Cliente.class));
        when(cajaService.extraerCajas(anyString(), anyInt())).thenReturn(Collections.singletonList(mock(Caja.class)));
        when(repartidorService.dequeue()).thenReturn(mock(Repartidor.class));
        when(vehiculoService.dequeue()).thenReturn(null);
        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpi");
        dto.setItems(Arrays.asList(new PedidoItemDTO("p", 1)));
        dto.setTipoAsignacion("AUTO");
        Pedido p = service.crearPedido(dto);

        // Ahora stub para asignar lo que falte
        Vehiculo veh2 = mock(Vehiculo.class);
        when(vehiculoService.dequeue()).thenReturn(veh2);

        Pedido actualizado = service.asignarRecursosAutomatico(p.getNumeroPedido());
        assertSame(veh2, actualizado.getVehiculo());
        assertEquals("EnCurso", actualizado.getEstado());
    }

    @Test
    public void testCompletarPedido() {
        // Hacer un pedido con recursos completos
        when(clienteService.buscar("dpi")).thenReturn(mock(Cliente.class));
        when(cajaService.extraerCajas(anyString(), anyInt())).thenReturn(Collections.singletonList(mock(Caja.class)));
        when(repartidorService.dequeue()).thenReturn(mock(Repartidor.class));
        when(vehiculoService.dequeue()).thenReturn(mock(Vehiculo.class));
        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpi");
        dto.setItems(Arrays.asList(new PedidoItemDTO("p", 1)));
        dto.setTipoAsignacion("AUTO");
        Pedido p = service.crearPedido(dto);

        boolean ok = service.completarPedido(p.getNumeroPedido());
        assertTrue(ok);
        assertEquals("Completado", p.getEstado());
        verify(repartidorService).reenqueue(p.getRepartidor());
        verify(vehiculoService).reenqueue(p.getVehiculo());
    }

    @Test
    public void testCancelarPedido() {
        // Crear pedido en curso
        when(clienteService.buscar("dpi")).thenReturn(mock(Cliente.class));
        when(cajaService.extraerCajas(anyString(), anyInt())).thenReturn(Collections.singletonList(mock(Caja.class)));
        when(repartidorService.dequeue()).thenReturn(mock(Repartidor.class));
        when(vehiculoService.dequeue()).thenReturn(mock(Vehiculo.class));
        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpi");
        dto.setItems(Arrays.asList(new PedidoItemDTO("p", 1)));
        dto.setTipoAsignacion("AUTO");
        Pedido p = service.crearPedido(dto);

        boolean ok = service.cancelarPedido(p.getNumeroPedido());
        assertTrue(ok);
        assertEquals("Cancelado", p.getEstado());
        verify(cajaService).reencolarCaja(anyString(), anyLong(), any());
        verify(repartidorService).reenqueue(p.getRepartidor());
        verify(vehiculoService).reenqueue(p.getVehiculo());
    }

    @Test
    public void testListarYEliminarPedidoPorEstado() {
        // Un solo pedido pendiente por AUTO sin recursos
        when(clienteService.buscar("dpi")).thenReturn(mock(Cliente.class));
        when(cajaService.extraerCajas(anyString(), anyInt())).thenReturn(Collections.singletonList(mock(Caja.class)));
        when(repartidorService.dequeue()).thenReturn(null);
        when(vehiculoService.dequeue()).thenReturn(null);
        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpi");
        dto.setItems(Arrays.asList(new PedidoItemDTO("p", 1)));
        dto.setTipoAsignacion("AUTO");
        Pedido p = service.crearPedido(dto);

        List<Pedido> pendientes = service.listarPedidosPorEstado("Pendiente");
        assertEquals(1, pendientes.size());

        // No debe eliminar pendiente
        assertFalse(service.eliminarPedido(p.getNumeroPedido()));

        // Marcar completado y eliminar
        p.setEstado("Completado");
        assertTrue(service.eliminarPedido(p.getNumeroPedido()));
        assertNull(service.buscarPedido(p.getNumeroPedido()));
    }

    @Test
    public void testObtenerDiagramaPedidosDTO() {
        // Dos pedidos distintos
        when(clienteService.buscar(anyString())).thenReturn(mock(Cliente.class));
        when(cajaService.extraerCajas(anyString(), anyInt())).thenReturn(Collections.singletonList(mock(Caja.class)));
        when(repartidorService.dequeue()).thenReturn(mock(Repartidor.class));
        when(vehiculoService.dequeue()).thenReturn(mock(Vehiculo.class));

        PedidoDTO dto = new PedidoDTO();
        dto.setDpiCliente("dpi");
        dto.setItems(Arrays.asList(new PedidoItemDTO("p", 1)));
        dto.setTipoAsignacion("AUTO");
        Pedido p1 = service.crearPedido(dto);
        Pedido p2 = service.crearPedido(dto);

        DiagramDTO d = service.obtenerDiagramaPedidosDTO();
        // Debe haber 2 nodos y 1 arista
        assertEquals(2, d.getNodes().size());
        assertEquals(1, d.getEdges().size());

        // Nodo 0 corresponde a p1
        assertEquals(0, d.getNodes().get(0).getId());
        assertEquals(String.valueOf(p1.getNumeroPedido()), d.getNodes().get(0).getLabel());
        // Arista de 0→1
        assertEquals(0, d.getEdges().get(0).getFrom());
        assertEquals(1, d.getEdges().get(0).getTo());
    }

    @Test
    public void testCargarDesdeCsv() {
        // Prepara datos: origen,destino,dpiCliente
        List<String[]> datos = Arrays.asList(
                new String[]{"A","B","dpi1"},
                new String[]{"C","D","dpi2"}
        );
        when(clienteService.buscar("dpi1")).thenReturn(mock(Cliente.class));
        when(clienteService.buscar("dpi2")).thenReturn(mock(Cliente.class));
        when(repartidorService.dequeue()).thenReturn(mock(Repartidor.class));
        when(vehiculoService.dequeue()).thenReturn(mock(Vehiculo.class));

        int count = service.cargarDesdeCsv(datos);
        assertEquals(2, count);

        // Ambos pedidos quedaron en estado EnCurso por tener ambos recursos
        List<Pedido> todos = service.listarPedidosPorEstado(null);
        assertEquals(2, todos.size());
        for (Pedido p : todos) {
            assertEquals("EnCurso", p.getEstado());
        }
    }
}
