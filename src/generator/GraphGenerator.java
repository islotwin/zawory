package generator;

import graph.Edge;
import graph.Graph;
import graph.Node;
import graph.NodeTypeEnum;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class GraphGenerator {
    public static Graph generateGraph(final Integer sources, final Integer valves, final Integer links, final Integer edges, final Integer toCut){
        final Graph graph = new Graph();
        final Set<Node> nodes = graph.getNodes();
        final Integer size = sources + valves + links;
        generateNodes(nodes, 0, sources, NodeTypeEnum.S);
        generateNodes(nodes, sources, links/2, NodeTypeEnum.L);
        generateNodes(nodes, sources + links/2, (valves/2), NodeTypeEnum.V);
        generateEdgesSource(graph, edges/2);

        generateNodes(nodes, sources + valves/2 + links/2, (valves/2), NodeTypeEnum.V);
        generateNodes(nodes, sources + valves + links/2, links/2 + toCut, NodeTypeEnum.L);
        final Integer sink = size - toCut;
        generateEdgesSink(graph, edges/2, sink, sources + (links/2));

        graph.getNodesToCut().addAll(getToCutIds(graph.getNodes(), sink));
        return graph;
    }

    private static void generateNodes(final Set<Node> nodes, final Integer id, final Integer nodesNumber, final NodeTypeEnum type){
        for (int i = id; i < id + nodesNumber; i++){
            nodes.add(new Node(i, type));
        }
    }

    //generate edges incoming to sink and edges up to edgesNumber
    private static void generateEdgesSink(final Graph graph, final Integer edgesNumber, final Integer sink, final Integer lowerBound){
        final List<Node> sinks = graph.getNodes().stream()
                .filter(n -> n.getId() >= sink)
                .collect(Collectors.toList());
        final List<Node> rest = graph.getNodes().stream()
                .filter(n -> n.getType() != NodeTypeEnum.S && n.getId() < sink)
                .collect(Collectors.toList());
        final Integer sourcesNumber = (int) graph.getNodes().stream().filter(n -> n.getType() == NodeTypeEnum.S).count();
        final Set<Edge> edges = graph.getEdges();

        //first generate edges from sources
        final Integer sourcesEdges = Double.valueOf(0.05 * edgesNumber).intValue();
        for(int i = 0; i < sourcesEdges; i++){
            int from = ThreadLocalRandom.current().nextInt(lowerBound - sourcesNumber, rest.size());
            int to = ThreadLocalRandom.current().nextInt(0, sinks.size());

            final Node fn = rest.get(from);
            final Node tn = sinks.get(to);
            final Edge edge = new Edge(fn, tn);
            fn.getOutput().add(edge);
            tn.getInput().add(edge);
            edges.add(edge);
        }

        //generate rest of the edges
        for(int i = sourcesEdges; i < edgesNumber; i++){
            int from, to;
            Node fn, tn;
            Edge edge;
            do{
                from = ThreadLocalRandom.current().nextInt(0, rest.size());
                to = ThreadLocalRandom.current().nextInt(0, rest.size());
                fn = rest.get(from);
                tn = rest.get(to);
                edge = new Edge(fn, tn);
            } while (to == from || fn.getOutput().contains(edge) || graph.getEdges().contains(edge));
            fn.getOutput().add(edge);
            tn.getInput().add(edge);
            edges.add(edge);
        }
    }

    //generate first half of edges - connected only with first half of the generated graph
    private static void generateEdgesSource(final Graph graph, final Integer edgesNumber){
        final List<Node> sources = graph.getNodes().stream()
                .filter(n -> n.getType() == NodeTypeEnum.S)
                .collect(Collectors.toList());
        final Integer sourcesNumber = sources.size();
        final List<Node> rest = graph.getNodes().stream()
                .filter(n -> n.getType() != NodeTypeEnum.S)
                .collect(Collectors.toList());
        final Integer restNumber = rest.size();
        final Set<Edge> edges = graph.getEdges();

        //first generate edges from sources
        final Integer sourcesEdges = Double.valueOf(0.05 * edgesNumber).intValue();
        for(int i = 0; i < sourcesEdges; i++){
            Node fn, tn;
            Edge edge;
            int from, to;
            do{
                from = ThreadLocalRandom.current().nextInt(0, sourcesNumber);
                to = ThreadLocalRandom.current().nextInt(0, restNumber);
                fn = sources.get(from);
                tn = rest.get(to);
                edge = new Edge(fn, tn);
            } while (to == from || fn.getOutput().contains(edge) || graph.getEdges().contains(edge));
            fn.getOutput().add(edge);
            tn.getInput().add(edge);
            edges.add(edge);
        }

        //generate rest of the edges
        for(int i = sourcesEdges; i < edgesNumber; i++){
            int from, to;
            Node fn, tn;
            Edge edge;
            do{
                from = ThreadLocalRandom.current().nextInt(0, restNumber);
                to = ThreadLocalRandom.current().nextInt(0, restNumber);
                fn = rest.get(from);
                tn = rest.get(to);
                edge = new Edge(fn, tn);
            } while (to == from || fn.getOutput().contains(edge) || graph.getEdges().contains(edge));
            fn.getOutput().add(edge);
            tn.getInput().add(edge);
            edges.add(edge);
        }
    }

    private static Set<Integer> getToCutIds(final Set<Node> nodes, final Integer sink){
        return nodes.stream()
                .filter(n -> n.getId() > sink)
                .map(n -> n.getId())
                .collect(Collectors.toSet());
    }

    public static void printGraph(final Graph graph){
        graph.getNodes().forEach(n -> System.out.print(n.getId() + " " + n.getType() + "\n"));
        System.out.print("-\n");
        graph.getEdges().forEach(e -> System.out.print(e.getFrom().getId() + " " + e.getTo().getId() + "\n"));
        System.out.print("-\n");
        graph.getNodesToCut().forEach(i -> System.out.print(i + " "));
        System.out.print("\n");
    }
}
