package com.ap.enlatados.service.eddlineales;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PilaTest {

    private Pila<Integer> pila;

    @Before
    public void setUp() {
        pila = new Pila<>();
    }

    @Test
    public void testPopOnEmpty() {
        assertNull("pop de pila vacía debe devolver null", pila.pop());
        assertTrue("isEmpty debe ser true", pila.isEmpty());
        assertTrue("toList debe devolver lista vacía", pila.toList().isEmpty());
    }

    @Test
    public void testPushAndPopOrder() {
        pila.push(1);
        pila.push(2);
        pila.push(3);

        assertEquals(Integer.valueOf(3), pila.pop());
        assertEquals(Integer.valueOf(2), pila.pop());
        assertEquals(Integer.valueOf(1), pila.pop());
        assertNull("Después de vaciar, pop debe devolver null", pila.pop());
    }

    @Test
    public void testPeekDoesNotModify() {
        pila.push(10);
        Integer top = pila.peek();
        assertEquals(Integer.valueOf(10), top);
        // peek no debe remover
        assertFalse(pila.isEmpty());
        assertEquals(Integer.valueOf(10), pila.pop());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(pila.isEmpty());
        pila.push(5);
        assertFalse("isEmpty debe ser false tras push", pila.isEmpty());
        pila.pop();
        assertTrue("isEmpty debe ser true tras pop último", pila.isEmpty());
    }

    @Test
    public void testToListReflectsStack() {
        // Pila: top=3 → [3,2,1]
        pila.push(1);
        pila.push(2);
        pila.push(3);
        List<Integer> expected = Arrays.asList(3,2,1);
        assertEquals(expected, pila.toList());
        // Tras pop
        pila.pop();
        assertEquals(Arrays.asList(2,1), pila.toList());
    }
}
