
import graph.Edge;
import graph.Graph;
import graph.Node;
import graph.NodeTypeEnum;

import java.util.Scanner;

public abstract class Parser {
    public static Graph parseFile() {
        final Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        final Graph graph = new Graph();

        //parse nodes types
        while (scanner.hasNext("\\d+\\ [svlSVL]")) {
            final String[] line = scanner.nextLine().split("\\ +");
            final Node node = new Node(Integer.parseInt(line[0]), NodeTypeEnum.valueOf(line[1].toUpperCase()));
            graph.getNodes().add(node);
        }

        scanner.nextLine();

        //parse edges
        while (scanner.hasNext("(\\d+\\ \\d+)")) {
            final String[] line = scanner.nextLine().split("\\ +");
            final Node from = graph.findNode(Integer.parseInt(line[0]));
            final Node to = graph.findNode(Integer.parseInt(line[1]));
            final Edge edge = new Edge(from, to);
            graph.getEdges().add(edge);
            from.getOutput().add(edge);
            to.getInput().add(edge);
        }

        scanner.nextLine();

        //parse nodes to cut
        final String[] line = scanner.next("\\d+(\\ +\\d+)*").split("\\ ");
        for (final String s : line) {
            graph.getNodesToCut().add(Integer.parseInt(s));
        }
        scanner.close();

        return graph;
    }

}