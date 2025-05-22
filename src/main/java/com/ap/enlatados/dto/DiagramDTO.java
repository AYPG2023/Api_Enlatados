package com.ap.enlatados.dto;

import java.util.List;

public class DiagramDTO {
    private List<NodeDTO> nodes;
    private List<EdgeDTO> edges;

    public DiagramDTO() {}

    public DiagramDTO(List<NodeDTO> nodes, List<EdgeDTO> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<NodeDTO> getNodes() { return nodes; }
    public void setNodes(List<NodeDTO> nodes) { this.nodes = nodes; }

    public List<EdgeDTO> getEdges() { return edges; }
    public void setEdges(List<EdgeDTO> edges) { this.edges = edges; }
}
