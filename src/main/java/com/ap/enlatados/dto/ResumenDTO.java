package com.ap.enlatados.dto;

public class ResumenDTO {
    private String producto;
    private long cantidad;
    private String fechaUltima;

    public ResumenDTO(String producto, long cantidad, String fechaUltima) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.fechaUltima = fechaUltima;
    }
    
    public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	public long getCantidad() { return cantidad; }
    public void setCantidad(long cantidad) { this.cantidad = cantidad; }

    public String getFechaUltima() { return fechaUltima; }
    public void setFechaUltima(String fechaUltima) { this.fechaUltima = fechaUltima; }
}
