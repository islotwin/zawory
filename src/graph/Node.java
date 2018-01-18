package graph;

import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Node {
    private final Set<Edge> input;
    private final Set<Edge> output;
    private Integer id;
    private NodeTypeEnum type;

    public Node(final Integer id, final NodeTypeEnum type){
        this.id = id;
        this.type = type;
        this.input = new HashSet<>();
        this.output = new HashSet<>();
    }
}

