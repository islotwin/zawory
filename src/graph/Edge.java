package graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = {"from", "to"})
public class Edge {
    private final Node from;
    private final Node to;

    public Edge(final Node from, final Node to) {
        this.from = from;
        this.to = to;
    }
}
