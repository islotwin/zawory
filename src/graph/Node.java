package graph;

import java.util.HashSet;
import java.util.Set;

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

    public Set<Edge> getInput(){
        return input;
    }

    public Set<Edge> getOutput(){
        return output;
    }

    public Integer getId(){
        return id;
    }

    public NodeTypeEnum getType(){
        return type;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Node)) {
            return false;
        }

        Node node = (Node) o;

        return id.equals(node.getId());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        result = 31 * result + type.hashCode();

        return result;
    }
}

