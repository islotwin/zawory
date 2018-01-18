package graph;


import java.util.HashSet;
import java.util.Set;

public class Graph {
    private final Set<Node> nodes;
    private final Set<Edge> edges;
    private final Set<Integer> nodesToCut;

    public Graph(){
     this.nodes = new HashSet<>();
     this.edges = new HashSet<>();
     this.nodesToCut  = new HashSet<>();
    }

    public Node findNode(final Integer id){
        return nodes.stream()
                .filter(n -> id.equals(n.getId()))
                .findAny()
                .orElseThrow(() -> new RuntimeException());
    }

    public Set<Node> getNodes(){
        return nodes;
    }

    public Set<Edge> getEdges(){
        return edges;
    }

    public Set<Integer> getNodesToCut(){
        return nodesToCut;
    }
}
