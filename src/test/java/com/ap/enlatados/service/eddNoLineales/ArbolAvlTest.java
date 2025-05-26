package com.ap.enlatados.service.eddNoLineales;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ArbolAvlTest {
    private ArbolAvl<Integer, Integer> tree;

    @Before
    public void setUp() {
        // clave = valor
        tree = new ArbolAvl<>(i -> i);
    }

    @Test
    public void testInsertAndInOrderSimple() {
        tree.insert(2);
        tree.insert(1);
        tree.insert(3);
        List<Integer> list = tree.inOrder();
        // Debe estar ordenado 1,2,3
        assertArrayEquals(new Integer[]{1,2,3}, list.toArray(new Integer[0]));
    }

    @Test
    public void testLeftRotation() {
        // Insertar en orden ascendente para forzar rotación simple a la izquierda
        tree.insert(1);
        tree.insert(2);
        tree.insert(3);  // aquí debería rotar
        List<Integer> list = tree.inOrder();
        assertArrayEquals(new Integer[]{1,2,3}, list.toArray(new Integer[0]));
        // El root ahora debe ser 2
        assertEquals(Integer.valueOf(2), tree.getRoot().key);
    }

    @Test
    public void testRightRotation() {
        // Insertar en orden descendente para forzar rotación simple a la derecha
        tree.insert(3);
        tree.insert(2);
        tree.insert(1);  // aquí debería rotar
        List<Integer> list = tree.inOrder();
        assertArrayEquals(new Integer[]{1,2,3}, list.toArray(new Integer[0]));
        // El root ahora debe ser 2
        assertEquals(Integer.valueOf(2), tree.getRoot().key);
    }

    @Test
    public void testLeftRightRotation() {
        // Caso LR: insertar 3,1,2
        tree.insert(3);
        tree.insert(1);
        tree.insert(2);  // genera rotación izquierda en 1, luego derecha en 3
        List<Integer> list = tree.inOrder();
        assertArrayEquals(new Integer[]{1,2,3}, list.toArray(new Integer[0]));
        assertEquals(Integer.valueOf(2), tree.getRoot().key);
    }

    @Test
    public void testRightLeftRotation() {
        // Caso RL: insertar 1,3,2
        tree.insert(1);
        tree.insert(3);
        tree.insert(2);  // genera rotación derecha en 3, luego izquierda en 1
        List<Integer> list = tree.inOrder();
        assertArrayEquals(new Integer[]{1,2,3}, list.toArray(new Integer[0]));
        assertEquals(Integer.valueOf(2), tree.getRoot().key);
    }

    @Test
    public void testFindExistingAndNonExisting() {
        tree.insert(5);
        tree.insert(10);
        assertEquals(Integer.valueOf(10), tree.find(10));
        assertNull(tree.find(999));
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteNonExistingThrows() {
        tree.delete(42);
    }

    @Test
    public void testDeleteLeaf() {
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);
        tree.delete(5);
        List<Integer> list = tree.inOrder();
        assertArrayEquals(new Integer[]{10,15}, list.toArray(new Integer[0]));
    }

    @Test
    public void testDeleteNodeWithTwoChildren() {
        // Construimos árbol con root 20, hijos 10 y 30
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        // Agregamos un hijo al 10 para que tenga dos hijos tras borrar 10
        tree.insert(5);
        tree.insert(15);
        // Ahora eliminamos 10 (nodo con dos hijos)
        tree.delete(10);
        List<Integer> list = tree.inOrder();
        // Debe ser [5,15,20,30]
        assertArrayEquals(new Integer[]{5,15,20,30}, list.toArray(new Integer[0]));
    }

    @Test
    public void testMultipleInsertDeleteKeepsBalance() {
        // Insertar múltiplos
        for (int i = 1; i <= 50; i++) {
            tree.insert(i);
        }
        // Luego eliminar algunos
        for (int i = 1; i <= 25; i++) {
            tree.delete(i);
        }
        List<Integer> list = tree.inOrder();
        // Debe contener del 26 al 50
        Integer[] expected = new Integer[25];
        for (int i = 0; i < 25; i++) expected[i] = i+26;
        assertArrayEquals(expected, list.toArray(new Integer[0]));
        // Verificamos que el árbol siga balanceado: |altura(left)-altura(right)| ≤ 1
        assertTrue(isBalanced(tree.getRoot()));
    }

    // Helper recursivo para comprobar balance en cada nodo
    private boolean isBalanced(AvlNode<Integer,Integer> node) {
        if (node == null) return true;
        int lh = node.left  == null ? 0 : node.left.height;
        int rh = node.right == null ? 0 : node.right.height;
        if (Math.abs(lh - rh) > 1) return false;
        return isBalanced(node.left) && isBalanced(node.right);
    }
}
