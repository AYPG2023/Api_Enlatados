package com.ap.enlatados.service.eddlineales;

import java.util.*;
import java.util.function.Predicate;

public class Cola<T> {
 private final Deque<T> deque = new ArrayDeque<>();

 public void enqueue(T item) { deque.addLast(item); }
 public T dequeue()       { return deque.pollFirst(); }
 public List<T> toList()  { return new ArrayList<>(deque); }
 public void removeIf(Predicate<T> pred) { deque.removeIf(pred); }
}
