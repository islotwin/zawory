package flowNetwork;

import graph.Graph;
import graph.Node;
import graph.NodeTypeEnum;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class FlowNetwork {
    private final FlowNode.STFlowNode source;
    private final FlowNode.STFlowNode sink;
    private final Set<FlowNode> nodes;
    private final Set<FlowEdge> edges;

    public FlowNetwork(final Graph graph) {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();

        sink = new FlowNode.STFlowNode(Integer.MIN_VALUE, NodeTypeEnum.L);
        buildSink(graph);

        source = new FlowNode.STFlowNode(Integer.MAX_VALUE, NodeTypeEnum.S);
        buildSource(graph);

        //build nodes and edges in network
        final Set<FlowNode> sourceOutputNodes = new HashSet(nodes);
        final Set<Integer> visited = new HashSet<>();
        visited.add(source.getId());
        sourceOutputNodes.stream().forEach(n -> addNodesAndEdges(graph, n, visited));
        deleteRedundantNodes();
    }

    public FlowNode.STFlowNode getSource(){
        return source;
    }

    public FlowNode.STFlowNode getSink(){
        return sink;
    }

    public Set<FlowNode> getNodes(){
        return nodes;
    }

    public Set<FlowEdge> getEdges(){
        return edges;
    }

    public void printNetwork(){
        System.out.print("NODES ");
        printOutputNodes(source);
        System.out.print("\n");
        for (FlowNode node : nodes) {
            System.out.print(node.getId() + "|" + node.getId() + " : ");
            printOutputNodes(node);
            System.out.print("\n");
        }
    }

    public long countValves(){
        return nodes.stream()
                .filter(n -> n.getType() == NodeTypeEnum.V)
                .count();
    }

    private void printOutputNodes(final FlowNode node){
        for (FlowEdge e : node.getOutput()) {
            System.out.print(e.getTo().getId() + " ");
        }
    }

    private void addSourceOutput(final Graph graph){
        source.getNodes().stream()
                .map(n -> n.getOutput())
                .flatMap(f -> f.stream())
                .forEach(f -> {
                    final Integer id = f.getTo().getId();
                    final NodeTypeEnum type = graph.findNode(id).getType();
                    final FlowNode n = findFlowNode(id).orElseGet(() -> {
                        final FlowNode fn = new FlowNode(id, 1, type);
                        nodes.add(fn);
                        return fn;
                    });
                    findOrCreateFlowEdge(source, n);
                });
    }

    private void deleteRedundantNodes(){
        new HashSet<>(nodes).stream()
                .filter(n -> n.getOutput().isEmpty())
                .forEach(n -> deleteNoOutputNodes(n));
        nodes.removeIf(n -> n.getInput().isEmpty() && !n.equals(source));
    }

    //delete nodes which have no output edges
    private void deleteNoOutputNodes(final FlowNode flowNode){
        if(nodes.contains(flowNode) && flowNode.getOutput().isEmpty()) {
            final Set<FlowEdge> input = new HashSet<>(flowNode.getInput());
            input.forEach(e -> {
                if(flowNode.getInput().contains(e)) {
                    final FlowNode from = e.getFrom();
                    from.getOutput().stream()
                            .filter(i -> i.equals(e))
                            .findAny()
                            .ifPresent(a -> from.getOutput().remove(a));
                    edges.remove(e);
                    flowNode.getInput().remove(e);
                    from.getOutput().remove(e);
                    deleteNoOutputNodes(from);
                }});
        }
    }

    private void addNodesAndEdges(final Graph graph, final FlowNode flowNode, final Set<Integer> visited){
        final Node node = graph.findNode(flowNode.getId());
        if(!visited.contains(node.getId())) {
            visited.add(node.getId());
            node.getOutput().stream()
                    .forEach(f -> {
                        final Integer id = f.getTo().getId();
                        if (!sink.getNodes().stream().anyMatch(s -> s.getId().equals(id))) {
                            final FlowNode n = findFlowNode(id).orElseGet(() -> {
                                final FlowNode fn = new FlowNode(id, 0, graph.findNode(id).getType());
                                nodes.add(fn);
                                return fn;
                            });

                            findOrCreateFlowEdge(flowNode, n);
                            addNodesAndEdges(graph, n, visited);
                        } else
                            findOrCreateFlowEdge(flowNode, sink);
                    });
        }
    }

    //create sink in network from nodes in graph marked as to cut
    private void buildSink(final Graph graph){
        final Set<Node> sinkNodes = graph.getNodesToCut().stream()
                .map(id -> graph.findNode(id))
                .collect(Collectors.toSet());

        if(sinkNodes.stream().anyMatch(c -> c.getType() == NodeTypeEnum.S ||
                c.getInput().stream().anyMatch(e -> e.getFrom().getType() == NodeTypeEnum.S))) {
            exit(1);
        }
        sink.getNodes().addAll(sinkNodes);
    }

    //create source in network from nodes in graph of type source
    private void buildSource(final Graph graph){
        final Set<Node> sourceNodes = graph.getNodes().stream()
                .filter(n -> n.getType() == NodeTypeEnum.S)
                .collect(Collectors.toSet());

        if(sourceNodes.stream().anyMatch(s -> !s.getInput().isEmpty()))
            exit(1);
        source.getNodes().addAll(sourceNodes);

        addSourceOutput(graph);
    }

    private FlowEdge findOrCreateFlowEdge(final FlowNode from, final FlowNode to){
        return findFlowEdge(from, to).orElseGet(() -> {
            final FlowEdge fe = new FlowEdge(from, to, Integer.MAX_VALUE);
            edges.add(fe);
            from.getOutput().add(fe);
            to.getInput().add(fe);
            return fe;
        });
    }

    private Optional<FlowNode> findFlowNode(final Integer id){
        return nodes.stream().filter(n -> n.getId().equals(id)).findAny();
    }

    private Optional<FlowEdge> findFlowEdge(final FlowNode from, final FlowNode to){
        return edges.stream().filter(e -> e.getFrom().equals(from) && e.getTo().equals(to)).findAny();
    }
}
