package com.ap.enlatados.service.eddlineales;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ColaTest {

    private Cola<Integer> cola;

    @Before
    public void setUp() {
        cola = new Cola<>();
    }

    @Test
    public void testDequeueOnEmpty() {
        // Al inicio, la cola está vacía
        assertNull("Dequeue de cola vacía debe devolver null", cola.dequeue());
        assertTrue("toList de cola vacía debe ser lista vacía", cola.toList().isEmpty());
    }

    @Test
    public void testEnqueueAndDequeueOrder() {
        cola.enqueue(1);
        cola.enqueue(2);
        cola.enqueue(3);

        assertEquals("Debe respetar orden FIFO: primero 1", Integer.valueOf(1), cola.dequeue());
        assertEquals("Luego debe devolver 2", Integer.valueOf(2), cola.dequeue());
        assertEquals("Luego debe devolver 3", Integer.valueOf(3), cola.dequeue());
        assertNull("Una vez vacía, dequeue debe devolver null", cola.dequeue());
    }

    @Test
    public void testToListReflectsCurrentContents() {
        cola.enqueue(10);
        cola.enqueue(20);
        cola.enqueue(30);

        List<Integer> expected = Arrays.asList(10, 20, 30);
        List<Integer> actual = cola.toList();
        assertEquals("toList debe devolver elementos en orden de entrada", expected, actual);

        // Después de un dequeue, toList debe actualizarse
        cola.dequeue(); // extrae 10
        expected = Arrays.asList(20, 30);
        actual = cola.toList();
        assertEquals("toList tras un dequeue debe reflejar la nueva lista", expected, actual);
    }

    @Test
    public void testRemoveIfPredicate() {
        cola.enqueue(1);
        cola.enqueue(2);
        cola.enqueue(3);
        cola.enqueue(4);

        // Eliminamos los pares
        cola.removeIf(i -> i % 2 == 0);

        List<Integer> expected = Arrays.asList(1, 3);
        assertEquals("removeIf debe eliminar todos los elementos que cumplan el predicado", expected, cola.toList());
    }

    @Test
    public void testRemoveIfAll() {
        cola.enqueue(5);
        cola.enqueue(6);

        // Eliminamos todo (predicado siempre true)
        cola.removeIf(i -> true);
        assertEquals("Si removeIf elimina todo, la cola debe quedar vacía", Collections.emptyList(), cola.toList());
        assertNull("Y dequeue sobre lista vacía debe devolver null", cola.dequeue());
    }
}
