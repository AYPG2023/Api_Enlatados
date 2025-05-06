package com.ap.enlatados.dto;

/**
 * Nodo recursivo para exponer la lista enlazada de usuarios.
 */
public class UsuarioNode {
    public Long id;
    public String nombre;
    public String apellidos;
    public String email;
    public UsuarioNode next;
}