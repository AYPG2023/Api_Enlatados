package com.ap.enlatados.service;

import com.ap.enlatados.entity.Caja;
import com.ap.enlatados.dto.ResumenDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CajaServiceTest {

    private CajaService service;
    private static final String PRODUCTO = "manzanas";

    @Before
    public void setUp() {
        service = new CajaService();
    }

    @Test
    public void testAgregarYListarCajas() {
        // Agrego 3 cajas
        List<Caja> creadas = service.agregarCajas(PRODUCTO, 3);
        assertEquals(3, creadas.size());

        // Listar debe devolverlas en orden LIFO (tope primero)
        List<Caja> lista = service.listarCajas(PRODUCTO);
        assertEquals(3, lista.size());
        // La última creada es la primera al listar
        assertEquals(creadas.get(2).getId(), lista.get(0).getId());
        assertEquals(creadas.get(1).getId(), lista.get(1).getId());
        assertEquals(creadas.get(0).getId(), lista.get(2).getId());
    }

    @Test
    public void testExtraerCajasReducirTamaño() {
        service.agregarCajas(PRODUCTO, 2);
        // Extraigo 1 caja
        List<Caja> sacadas = service.extraerCajas(PRODUCTO, 1);
        assertEquals(1, sacadas.size());

        // Ahora debe quedar sólo 1 caja en la pila
        List<Caja> restantes = service.listarCajas(PRODUCTO);
        assertEquals(1, restantes.size());
        // Y esa caja restante no es la misma que saqué
        assertNotEquals(sacadas.get(0).getId(), restantes.get(0).getId());
    }

    @Test
    public void testReencolarCajaMantieneFechaEId() {
        // Creo y saco una caja
        service.agregarCajas(PRODUCTO, 1);
        Caja original = service.listarCajas(PRODUCTO).get(0);
        service.extraerCajas(PRODUCTO, 1);

        // La reencolo con la misma fecha e ID
        service.reencolarCaja(PRODUCTO, original.getId(), original.getFechaIngreso());
        List<Caja> lista = service.listarCajas(PRODUCTO);
        assertEquals(1, lista.size());
        Caja reencolada = lista.get(0);
        assertEquals(original.getId(), reencolada.getId());
        assertEquals(original.getFechaIngreso(), reencolada.getFechaIngreso());
    }

    @Test
    public void testObtenerResumenDeProductos() {
        service.agregarCajas("A", 2);
        service.agregarCajas("B", 1);

        List<ResumenDTO> resumen = service.obtenerResumenDeProductos();
        // Debe contener dos entradas (A y B)
        assertEquals(2, resumen.size());

        for (ResumenDTO r : resumen) {
            if ("A".equals(r.getProducto())) {
                assertEquals(2L, r.getCantidad());
                assertNotNull("Fecha última no debe ser null", r.getFechaUltima());
            } else if ("B".equals(r.getProducto())) {
                assertEquals(1L, r.getCantidad());
                assertNotNull(r.getFechaUltima());
            } else {
                fail("Producto inesperado en resumen: " + r.getProducto());
            }
        }
    }

}
