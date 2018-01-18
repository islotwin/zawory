package generator;


import graph.Edge;
import graph.Graph;
import graph.Node;
import graph.NodeTypeEnum;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GraphGenerator {

    public static Graph generateGraph(final Integer sources, final Integer valves, final Integer links, final Integer edges, final Integer toCut){
        final Graph graph = new Graph();
        final Set<Node> nodes = graph.getNodes();
        generateNodes(nodes, 0, sources, NodeTypeEnum.S);
        generateNodes(nodes, sources, valves, NodeTypeEnum.V);
        generateNodes(nodes, sources + valves, links, NodeTypeEnum.L);

        generateEdges(graph, edges);

        graph.getNodesToCut().addAll(getToCutIds(graph.getNodes(), toCut));
        return graph;
    }

    private static void generateNodes(final Set<Node> nodes, final Integer id, final Integer nodesNumber, final NodeTypeEnum type){
        for (int i = id; i < id + nodesNumber; i++){
            nodes.add(new Node(i, type));
        }
    }

    private static void generateEdges(final Graph graph, final Integer edgesNumber){
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
        //final Integer sourcesEdges = ThreadLocalRandom.current().nextInt(0, edgesNumber);
        final Integer sourcesEdges = Double.valueOf(0.05 * edgesNumber).intValue();
        for(int i = 0; i < sourcesEdges; i++){
            int from = ThreadLocalRandom.current().nextInt(0, sourcesNumber);
            int to = ThreadLocalRandom.current().nextInt(0, restNumber);

            final Node fn = sources.get(from);
            final Node tn = rest.get(to);
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

    private static Set<Integer> getToCutIds(final Set<Node> nodes, final Integer toCut){
        return nodes.stream()
                .filter(n -> n.getType() != NodeTypeEnum.S && n.getInput().stream().allMatch(e -> e.getFrom().getType() != NodeTypeEnum.S))
                .map(n -> n.getId())
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                        Collections.shuffle(list);
                        return list;
                }))
                .stream()
                .limit(toCut)
                .collect(Collectors.toSet());
    }
}
