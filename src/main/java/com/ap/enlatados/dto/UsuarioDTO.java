package com.ap.enlatados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioDTO {
    @NotNull
    private Long id;

    @Size(min = 2, max = 50)
    private String nombre;

    @Size(min = 2, max = 50)
    private String apellidos;

    @Email @NotNull
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    public UsuarioDTO() { }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
