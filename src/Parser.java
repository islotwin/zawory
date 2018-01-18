
import graph.Edge;
import graph.Graph;
import graph.Node;
import graph.NodeTypeEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class Parser {
    public static Graph parseFile() {
       // final Scanner scanner = new Scanner(System.in);
        final File f = new File("/Users/iga/Desktop/STUDIA/SEM5/AAL/zawory/src/test7.txt");
        Graph graph = new Graph();
        //System.out.println(new File(".").getAbsoluteFile());
        try (Scanner scanner = new Scanner(f)) {

            scanner.useDelimiter("\n");
            //graph = new Graph();
            while (scanner.hasNext("\\d+\\ [svlSVL]")) {
                final String[] line = scanner.nextLine().split("\\ +");
                final Node node = new Node(Integer.parseInt(line[0]), NodeTypeEnum.valueOf(line[1].toUpperCase()));
                graph.getNodes().add(node);
            }

            scanner.nextLine();

            while (scanner.hasNext("(\\d+\\ \\d+)")) {
                final String[] line = scanner.nextLine().split("\\ +");
                final Node from = graph.findNode(Integer.parseInt(line[0]));
                final Node to = graph.findNode(Integer.parseInt(line[1]));
                final Edge edge = new Edge(from, to);
                graph.getEdges().add(edge);
                from.getOutput().add(edge);
                to.getInput().add(edge);
                //TODO if source has input -> error
            }

            scanner.nextLine();

            final String[] line = scanner.next("\\d+(\\ +\\d+)*").split("\\ ");
            for (final String s : line) {
                graph.getNodesToCut().add(Integer.parseInt(s));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return graph;
        //kosz
    }

}
