package com.ap.enlatados.service.eddlineales;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class ListaTest {

    private Lista<String> lista;

    @Before
    public void setUp() {
        lista = new Lista<>();
    }

    @Test
    public void testAddAndToList() {
        // Lista vacía al inicio
        assertTrue(lista.toList().isEmpty());

        lista.add("A");
        lista.add("B");
        lista.add("C");

        List<String> expected = Arrays.asList("A", "B", "C");
        assertEquals("toList debe reflejar el orden de inserción", expected, lista.toList());
    }

    @Test
    public void testFindExisting() {
        lista.add("foo");
        lista.add("bar");
        lista.add("baz");

        String found = lista.find(s -> s.startsWith("ba"));
        assertEquals("bar", found);
    }

    @Test
    public void testFindNonExisting() {
        lista.add("x");
        lista.add("y");
        assertNull("find debe devolver null si no hay coincidencias",
                lista.find(s -> s.contains("z")));
    }

    @Test
    public void testRemoveHead() {
        lista.add("head");
        lista.add("tail");
        boolean removed = lista.remove(s -> s.equals("head"));
        assertTrue("remove debe retornar true al eliminar head", removed);
        assertEquals(Collections.singletonList("tail"), lista.toList());
    }

    @Test
    public void testRemoveMiddle() {
        lista.add("one");
        lista.add("two");
        lista.add("three");
        boolean removed = lista.remove(s -> s.equals("two"));
        assertTrue("remove debe retornar true al eliminar elemento intermedio", removed);
        assertEquals(Arrays.asList("one", "three"), lista.toList());
    }

    @Test
    public void testRemoveNonExisting() {
        lista.add("a");
        lista.add("b");
        boolean removed = lista.remove(s -> s.equals("c"));
        assertFalse("remove debe retornar false si no se elimina nada", removed);
        assertEquals(Arrays.asList("a", "b"), lista.toList());
    }
}
