package com.ap.enlatados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioDTO {

    @Size(min = 2, max = 50)
    private String nombre;

    @Size(min = 2, max = 50)
    private String apellidos;

    @Email
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    public UsuarioDTO() { }

    // --- GETTERS ---
    public String getNombre() {
        return nombre;
    }
    public String getApellidos() {
        return apellidos;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    // --- SETTERS ---
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

