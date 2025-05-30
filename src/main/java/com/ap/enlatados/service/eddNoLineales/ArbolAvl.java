package com.ap.enlatados.service.eddNoLineales;

import java.util.*;
import java.util.function.Function;

/**
* Árbol AVL genérico, clave K comparable y se puede extraer en T.
*/
public class ArbolAvl<T, K extends Comparable<K>> {
 private AvlNode<T,K> root;
 private final Function<T,K> keyExtractor;

 public ArbolAvl(Function<T,K> keyExtractor) {
     this.keyExtractor = keyExtractor;
 }

 public void insert(T item) {
     K key = keyExtractor.apply(item);
     root = insert(root, key, item);
 }

 private AvlNode<T,K> insert(AvlNode<T,K> node, K key, T data) {
     if (node == null) return new AvlNode<>(key, data);
     int cmp = key.compareTo(node.key);
     if      (cmp < 0) node.left  = insert(node.left,  key, data);
     else if (cmp > 0) node.right = insert(node.right, key, data);
     else throw new IllegalArgumentException("Clave duplicada: " + key);

     updateHeight(node);
     return rebalance(node);
 }
 
 public AvlNode<T,K> getRoot() {
     return root;
 }

 public void delete(K key) {
     root = delete(root, key);
 }

 private AvlNode<T,K> delete(AvlNode<T,K> node, K key) {
     if (node == null) throw new NoSuchElementException("No encontrado: " + key);
     int cmp = key.compareTo(node.key);
     if      (cmp < 0) node.left  = delete(node.left,  key);
     else if (cmp > 0) node.right = delete(node.right, key);
     else {
         if (node.left == null)  return node.right;
         if (node.right == null) return node.left;
         AvlNode<T,K> succ = min(node.right);
         node.key  = succ.key;
         node.data = succ.data;
         node.right = delete(node.right, succ.key);
     }
     updateHeight(node);
     return rebalance(node);
 }

 private AvlNode<T,K> min(AvlNode<T,K> n) {
     while (n.left != null) n = n.left;
     return n;
 }

 private void updateHeight(AvlNode<T,K> n) {
     n.height = 1 + Math.max(height(n.left), height(n.right));
 }

 private int height(AvlNode<T,K> n) {
     return n == null ? 0 : n.height;
 }

 private int balance(AvlNode<T,K> n) {
     return n == null ? 0 : height(n.left) - height(n.right);
 }

 /*** Hace el balance entre nodos*/
 private AvlNode<T,K> rebalance(AvlNode<T,K> z) {
     int b = balance(z);
     //Hace un rotacion doble entre L Y R
     if (b > 1) {
         if (balance(z.left) < 0) z.left = rotateLeft(z.left);
         //Rotacion simple hacia la derecha
         return rotateRight(z);
     }
     if (b < -1) {
         //Hace un rotacion doble entre R Y L
         if (balance(z.right) > 0) z.right = rotateRight(z.right);
         //Rotacion simple hacia la izquierda.
         return rotateLeft(z);
     }
     return z; // Lo devuelve  ya balanceado.
 }

 /**Hace la rotacion hacia la derecha
     * */
 private AvlNode<T,K> rotateRight(AvlNode<T,K> y) {
     AvlNode<T,K> x = y.left;
     y.left = x.right;
     x.right = y;
     updateHeight(y);
     updateHeight(x);
     return x;
 }

 /**Hace la rotacion hacia la izquierda
  * */
 private AvlNode<T,K> rotateLeft(AvlNode<T,K> x) {
     AvlNode<T,K> y = x.right;
     x.right = y.left;
     y.left = x;
     updateHeight(x);
     updateHeight(y);
     return y;
 }

 /**Busca comparando las clavs hasta poder encontrar un nodo o llegar a null.
  * */
 public T find(K key) {
     AvlNode<T,K> n = findNode(root, key);
     return n == null ? null : n.data;
 }

 private AvlNode<T,K> findNode(AvlNode<T,K> node, K key) {
     if (node == null) return null;
     int cmp = key.compareTo(node.key);
     if      (cmp < 0) return findNode(node.left,  key);
     else if (cmp > 0) return findNode(node.right, key);
     else              return node;
 }


 /**Hace un recorrido en orden.
  * */
 public List<T> inOrder() {
     List<T> out = new ArrayList<>();
     traverse(root, out);
     return out;
 }

 private void traverse(AvlNode<T,K> node, List<T> out) {
     if (node == null) return;
     traverse(node.left,  out);
     out.add(node.data);
     traverse(node.right, out);
 }
}
