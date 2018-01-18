
import algorithms.BF;
import algorithms.Dinic;
import flowNetwork.FlowNetwork;
import generator.BetterGraphGenerator;
import generator.GraphGenerator;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class Main {

    public static void main(String[] args) {
        Graph graph;
        other();
        if(args.length == 0) {
            System.out.print(" \"-m1\" read main.java.zawory.graph from file\t< filename\n \"-m2\" generate graphs\tcount start jump repeat\"-m3\" read main.java.zawory.graph from file");
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
                final Double density = Double.parseDouble(args[2]);
                Double c1 = 0.1;
                Double c2 = 0.2;
                while(c1*valves >= 10){
                    c1*=0.1;
                    c2*=0.1;
                }
                Integer links = generateInt(0, valves*density);
                graph = GraphGenerator.generateGraph(generateInt(valves*c1, valves*c2), valves, links, generateInt((valves+links)*1.5, 4 * (links + valves)), generateInt(valves*c1, valves*c2));
                testDinic(graph, true);
                testBF(graph, true);
                break;
            case "-m3":
                final Integer count = Integer.parseInt(args[1]);
                final Integer start = Integer.parseInt(args[2]);
                final Integer jump = Integer.parseInt(args[3]);
                final Integer repeat = Integer.parseInt(args[4]);
                final List<Graph> graphs = new ArrayList<>();
                final List<Double> times = new ArrayList<>();
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
                    graphs.clear();
                    for(int j = 0; j < repeat; j++) {
                        graphs.add(GraphGenerator.generateGraph(generateInt(it*c1, it*c2), it, generateInt(it*0.1, it*0.4), generateInt(it*1.5, 4 * it), generateInt(it*c1, it*c2)));
                    }
                    for(Graph g : graphs){
                        if((temp = testDinic(g, false)) > 0){
                            denominator++;
                            average += temp;
                        }
                    }
                    if(denominator == 0)
                        times.add(-1.0);
                    else
                        times.add(((double)average/(double)denominator));
                    it += jump;
                }
                Integer nMedian = count%2 == 0 ? (2*start) + ((count-1)*jump) : (2*start) + (count*jump);
                Double median = times.get(count/2);
                Integer i = 0;
                it = start;
                System.out.printf("%-5s |%-20s |%-20s\n", "n", "t(n)", "q(n)");
                while(i < times.size()){
                    final Double value = times.get(i);
                    System.out.printf("%-5d |%-20.2f |%-20.2f\n", it, value, countQ((double) it, (double) nMedian, value, median));
                    it += jump;
                    i++;
                }
                break;
            default:
                System.out.print(" \"-m1\" read main.java.zawory.graph from file\t< filename\n \"-m2\" generate graphs\tcount start jump repeat\"-m3\" read main.java.zawory.graph from file");
            }
    }

    private static Double countQ(final Double n, final Double nMed, final Double value, final Double median){
        return (nMed/n)*(nMed/n)*(value/median);
    }

    private static Integer generateInt(final Number from, final Number to){
        return ThreadLocalRandom.current().nextInt(from.intValue(), to.intValue());
    }

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
        }
        if(solution.isEmpty())
            return 0l;
        return end;
    }

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

    private static void other(){
 /*       final Integer valves = 50;
        final Double density = 0.8;
        final Integer edges = density < 1 ? (int)((((valves * valves) - valves)*0.5)*density) : (int)(((valves * valves) - valves)*0.3);
        final Graph graph = BetterGraphGenerator.generateGraph(generateInt(valves*0.05, valves*0.2), valves, generateInt(valves*density, 2*valves), generateInt(valves*1.5, edges), generateInt(valves*0.1, valves*0.2));
        //final Graph graph = BetterGraphGenerator.generateGraph(10, 50, 100, 200, 5);
        System.out.print("Dinic :" + testDinic(graph, true));
        System.out.print("BF    :" + testBF(graph, true));
*/
        Graph graph;
        final Integer valves = 250;
        final Double density = 0.6;
        Double c1 = 0.1;
        Double c2 = 0.2;
        while(c1*valves >= 10){
            c1*=0.1;
            c2*=0.1;
        }
        graph = GraphGenerator.generateGraph(generateInt(valves*c1, valves*c2), valves, generateInt(valves, valves* 4), generateInt(valves*1.5, 4 * valves), generateInt(valves*c1, valves*c2));
        testDinic(graph, true);
        testBF(graph, true);

    }
}
