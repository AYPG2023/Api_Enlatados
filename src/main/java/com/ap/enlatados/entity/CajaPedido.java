package com.ap.enlatados.entity;

public class CajaPedido {
    private Long id;
    private String producto;
    private String fechaIngreso;

    /**
     * Constructor completo de CajaPedido.
     *
     * @param id           Identificador único de la caja
     * @param producto     Nombre o código del producto contenido
     * @param fechaIngreso Fecha de ingreso de la caja (ISO-8601)
     */
    public CajaPedido(Long id, String producto, String fechaIngreso) {
        this.id = id;
        this.producto = producto;
        this.fechaIngreso = fechaIngreso;
    }

    // Getters

    /** @return el ID de la caja */
    public Long getId() {
        return id;
    }

    /** @return el producto contenido en la caja */
    public String getProducto() {
        return producto;
    }

    /** @return la fecha de ingreso de la caja */
    public String getFechaIngreso() {
        return fechaIngreso;
    }

    // Setters (si necesitas mutabilidad)

    /** @param producto establece o actualiza el producto */
    public void setProducto(String producto) {
        this.producto = producto;
    }

    /** @param fechaIngreso establece o actualiza la fecha de ingreso */
    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return "CajaPedido{" +
               "id=" + id +
               ", producto='" + producto + '\'' +
               ", fechaIngreso='" + fechaIngreso + '\'' +
               '}';
    }
}
