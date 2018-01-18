package algorithms;


import flowNetwork.FlowEdge;
import flowNetwork.FlowNetwork;
import flowNetwork.FlowNode;
import graph.NodeTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

public class Dinic extends Algorithm {

    public Dinic(final FlowNetwork graph){
        super(graph);
        divideValves();
    }

    public Set<Integer> run(){
        resetFlow();
        final Integer total = calculateMaxFlow();
        final Set<FlowEdge> toRemove = graph.getEdges().stream()
                .filter(e -> e.getFrom().getLevel() != 0 && e.getTo().getLevel() == 0)
                .collect(Collectors.toSet());
        if(total == 0 || total == Integer.MAX_VALUE)
            return new HashSet<>();
        if(toRemove.size() > total) {
            if(hasSeparatingSubset((long)total, toRemove.stream().map(e -> e.getFrom()).collect(Collectors.toList())))
                return separatingNodes;
            //function to estimate separating valves
            //toRemove.retainAll(flattenSubset(toRemove));
        }
        return toRemove.stream()
                .map(e -> e.getFrom().getId())
                .collect(Collectors.toSet());
    }

    //check whether given set of valves cuts the network
    public boolean isSourceConnected(final Set<Integer> toRemove){
        resetFlow();
        graph.getNodes().stream()
                .filter(e ->toRemove.contains(e.getId()))
                .map(e -> e.getOutput())
                .flatMap(s -> s.stream())
                .forEach(e -> e.setFlow(e.getCapacity()));
        return setNodesLevels(graph.getSource());
    }

    public Integer getMaxFlow(){
        resetFlow();
        return calculateMaxFlow();
    }

    private Integer calculateMaxFlow(){
        Integer total = 0;
        final Long max = graph.countValves();
        while(setNodesLevels(graph.getSource())){
            Integer flow;
            while((flow  = sendFlow(graph.getSource(), Integer.MAX_VALUE)) > 0) {
                if (flow > max) {
                    return Integer.MAX_VALUE;
                }
                total += flow;
            }
        }
        return total;
    }

    private Integer sendFlow(final FlowNode node, final Integer flow){
        if(node.equals(graph.getSink()))
            return flow;
        for(FlowEdge e : node.getOutput()) {
            if(node.getLevel().equals(e.getTo().getLevel() - 1) && e.getFlow() < e.getCapacity()){
                final Integer curFlow = Math.min(flow, e.getCapacity() - e.getFlow());
                final Integer tempFlow = sendFlow(e.getTo(), curFlow);

                if(tempFlow > 0){
                    e.setFlow(e.getFlow() + tempFlow);

                    return tempFlow;
                }
            }
        }
        return 0;
    }

    //cut valves in half with one edge between them with the capacity of 1
    private void divideValves(){
        graph.getNodes().stream()
                .filter(n -> n.getType() == NodeTypeEnum.V)
                .collect(Collectors.toSet())
                .forEach(v -> {
                    final Integer outId = -1 * v.getId();
                    final FlowNode outNode = new FlowNode(outId, 0, v.getType());
                    graph.getNodes().add(outNode);
                    v.getOutput().forEach(e -> e.setFrom(outNode));
                    outNode.getOutput().addAll(v.getOutput());
                    v.getOutput().clear();
                    final FlowEdge edge = new FlowEdge(v, outNode, 1);
                    graph.getEdges().add(edge);
                    v.getOutput().add(edge);
                    outNode.getInput().add(edge);
                });
    }

    private Set<FlowEdge> flattenSubset(final Set<FlowEdge> toRemove){
        final Set<FlowNode> temp = toRemove.stream().map(e -> e.getTo()).collect(Collectors.toSet());
        return new HashSet<>(temp).stream()
                .filter(n -> !dfs(n, temp, new ArrayList<>(Arrays.asList(n))))
                .map(n -> n.getInput())
                .flatMap(s -> s.stream())
                .collect(Collectors.toSet());

    }

    //run dfs from given node to check wheter its children are all in subset of separating nodes
    private boolean dfs(final FlowNode node, final Set<FlowNode> toRemove, final List<FlowNode> visited){
        if(node.equals(graph.getSink()))
            return false;
        final Set<FlowNode> output = node.getOutput().stream()
                .map(e -> e.getTo())
                .filter(n -> !toRemove.contains(n))
                .collect(Collectors.toSet());
        if(output.isEmpty())
            return true;
        output.removeAll(visited);
        if(output.isEmpty())
            return false;
        for(FlowNode n : output){
            visited.add(0, n);
            if(!dfs(visited.get(0), toRemove, visited))
                return false;
        }
        return true;
    }
}
