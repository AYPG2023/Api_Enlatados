package com.ap.enlatados.dto;

public class NodeDTO {
    private int id;
    private String label;

    public NodeDTO() {}

    public NodeDTO(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
