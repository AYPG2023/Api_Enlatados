package com.ap.enlatados.service.eddNoLineales;

public class AvlNode<T, K extends Comparable<K>> {
	public K key;
    public T data;
    public AvlNode<T,K> left;
	public AvlNode<T,K> right;
    int height;

    public AvlNode(K key, T data) {
        this.key = key;
        this.data = data;
        this.height = 1;
    }
}