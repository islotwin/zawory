
import algorithms.BF;
import algorithms.Dinic;
import flowNetwork.FlowNetwork;
import generator.GraphGenerator;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class Main {

    public static void main(String[] args) {
        Graph graph;
        if(args.length == 0) {
            System.out.print("-m1\t\"read graph from file\t< filename\"\n-m2\t\"generate graph \tvalves density\"\n-m3\t\"generate graphs\tcount start jump repeat density\"\n");
            return;
        }
        switch (args[0]){
            case "-m1":
                graph = Parser.parseFile();
                testDinic(graph, true);
                testBF(graph, true);
                break;
            case "-m2":
                final Integer valves = Integer.parseInt(args[1]);
                Double density = Double.parseDouble(args[2]);
                Double c1 = 0.1;
                Double c2 = 0.3;
                while(c1*valves >= 10){
                    c1*=0.1;
                    c2*=0.1;
                }
                Integer links = generateInt(0, valves*density);
                graph = GraphGenerator.generateGraph(generateInt(valves*c1, valves*c2), valves, links, generateInt((valves + links)*1.5, (links + valves)*4), generateInt(valves*c1, valves*c2));
                GraphGenerator.printGraph(graph);
                testDinic(graph, true);
                testBF(graph, true);
                break;
            case "-m3":
                final Integer count = Integer.parseInt(args[1]);
                final Integer start = Integer.parseInt(args[2]);
                final Integer jump = Integer.parseInt(args[3]);
                final Integer repeat = Integer.parseInt(args[4]);
                density = Double.parseDouble(args[4]);
                final List<Graph> graphs = new ArrayList<>();
                final List<Double> times = new ArrayList<>();
                final List<Double> vertices = new ArrayList<>();
                final List<Double> edges = new ArrayList<>();
                Integer it = start;
                Integer denominator = 0;
                Long temp = 0l;
                c1 = 0.1;
                c2 = 0.2;
                while(c1*start >= 10){
                    c1*=0.1;
                    c2*=0.1;
                }
                for(int i = 0; i < count; i++){
                    denominator = 0;
                    Long average = 0l;
                    Long averageVertices = 0l;
                    Long averageEdges = 0l;
                    for(int j = 0; j < repeat; j++) {
                        links = generateInt(0, it*density);
                        Integer e = generateInt((it+links)*1.5, (it+links)*4);
                        graph = GraphGenerator.generateGraph(generateInt(it*c1, it*c2), it, links, e, generateInt(it*c1, it*c2));
                        if((temp = testDinic(graph, false)) > 0){
                            denominator++;
                            average += temp;
                            averageVertices += links;
                            averageEdges += e;
                        }
                    }
                    if(denominator == 0) {
                        times.add(-1.0);
                        vertices.add(-1.0);
                        edges.add(-1.0);
                    }
                    else {
                        times.add(((double) average / (double) denominator));
                        vertices.add(((double) averageVertices / (double) denominator) + it);
                        edges.add(((double) averageEdges / (double) denominator));
                    }
                    it += jump;
                }
                Double median, nMedian, eMedian;
                if(count % 2 == 1){
                    median = times.get(count/2);
                    nMedian = vertices.get(count/2);
                    eMedian = edges.get(count/2);
                }
                else{
                    median = (times.get(count/2) + times.get((count/2) - 1))/2;
                    nMedian = (vertices.get(count/2) + vertices.get((count/2) - 1))/2;
                    eMedian = (edges.get(count/2) + edges.get((count/2) - 1))/2;
                }
                Integer i = 0;
                it = start;
                System.out.printf("%-5s |%-20s |%-20s |%-20s |%-20s\n", "n", "vertices", "edges", "t(n)", "q(n)");
                while(i < times.size()){
                    final Double time = times.get(i);
                    final Double vert = vertices.get(i);
                    final Double edge = edges.get(i);
                    System.out.printf("%-5d |%-20.2f |%-20.2f |%-20.2f |%-20.2f\n", it, vert, edge, time, calculateQ(vert, nMedian, time, median, edge, eMedian));
                    it += jump;
                    i++;
                }
                break;
            default:
                System.out.print("-m1\t\"read graph from file\t< filename\"\n-m2\t\"generate graph \tvalves density\"\n-m3\t\"generate graphs\tcount start jump repeat density\"\n");
            }
    }

    private static Double calculateQ(final Double n, final Double nMed, final Double valTime, final Double medTime, final Double edge, final Double eMedian){
        return (edge/eMedian)*(nMed/n)*(nMed/n)*(valTime/medTime);
    }

    //generate random int
    private static Integer generateInt(final Number from, final Number to){
        return ThreadLocalRandom.current().nextInt(from.intValue(), to.intValue());
    }

    //run test with max flow problem
    private static Long testDinic(final Graph graph, boolean printSolution){
        long start = System.currentTimeMillis();
        final FlowNetwork network = new FlowNetwork(graph);
        final Dinic dinic = new Dinic(network);
        final Set<Integer> solution = dinic.run();
        long end = System.currentTimeMillis() - start;
        if(printSolution) {
            System.out.print("Dinic: " + end + " ms\n");
            System.out.print("{ ");
            solution.forEach(i -> System.out.print(i + " "));
            System.out.print("}\n");
            System.out.print("Max flow: " + dinic.getMaxFlow() + "\n");
            System.out.print("Nodes to cut are reachable: " + dinic.isSourceConnected(solution) + "\n");
        }
        if(solution.isEmpty())
            return 0l;
        return end;
    }

    //run bruteforce test
    private static Long testBF(final Graph graph, boolean printSolution){
        final long start = System.currentTimeMillis();
        final FlowNetwork networkBF = new FlowNetwork(graph);
        final BF bf = new BF(networkBF);
        final Set<Integer> solutionBF = bf.run();
        final long end = System.currentTimeMillis() - start;
        if(printSolution) {
            System.out.print("BF:    " + end + " ms\n");
            System.out.print("{ ");
            solutionBF.forEach(n -> System.out.print(n + " "));
            System.out.print("}\n");
        }
        return end;
    }

}
