package algorithms;


import flowNetwork.FlowEdge;
import flowNetwork.FlowNetwork;
import flowNetwork.FlowNode;
import graph.NodeTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

public class Dinic {
    private final FlowNetwork graph;
    private Integer total;
    private final Set<Integer> separatingNodes;

    public Dinic(final FlowNetwork graph){
        separatingNodes = new HashSet<>();
        total = 0;
        this.graph = graph;
        divideValves();
    }

    public boolean isSourceConnected(final Set<FlowEdge> toRemove){
        resetFlow();
        graph.getEdges().stream()
                .filter(e ->toRemove.contains(e))
                .forEach(e -> e.setFlow(e.getCapacity()));
        return setNodesLevels(graph.getSource());
    }

    public Integer getMaxFlow(){
        resetFlow();
        return findMaxFlow();
    }

    private Integer findMaxFlow(){
        Integer total = 0;
        final Long max = graph.countValves();
        while(setNodesLevels(graph.getSource())){
            Integer flow;
            while((flow  = sendFlow(graph.getSource(), Integer.MAX_VALUE)) > 0) {
                if (flow > max) {
                    //System.out.print("\nSink can not be cut from source");
                    return Integer.MAX_VALUE;
                }
                total += flow;
            }
        }
        return total;
    }

    public Set<Integer> run(){
        resetFlow();
        final Integer total = findMaxFlow();
        final Set<FlowEdge> toRemove = graph.getEdges().stream()
                .filter(e -> e.getFrom().getLevel() != 0 && e.getTo().getLevel() == 0)
                .collect(Collectors.toSet());
        System.out.print("MAX FLOW " + total + "\n");
        if(total == 0 || total == Integer.MAX_VALUE)
            return new HashSet<>();
        if(toRemove.size() > total) {
            final Set<FlowNode> temp = toRemove.stream().map(e -> e.getTo()).collect(Collectors.toSet());
            final Set<FlowNode> it = new HashSet<>(temp);
            final Set<FlowEdge> tempE = it.stream()
                    .filter(n -> !dfs(n, temp, new ArrayList<>(Arrays.asList(n))))
                    .map(n -> n.getInput())
                    .flatMap(s -> s.stream())
                    .collect(Collectors.toSet());
            toRemove.retainAll(tempE);
        }
        return toRemove.stream()
                .map(e -> e.getFrom().getId())
                .collect(Collectors.toSet());
    }

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

    private void resetFlow(){
        graph.getEdges().forEach(e -> e.setFlow(0));
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

    private boolean setNodesLevels(final FlowNode node){
        if(node.equals(graph.getSource())) {
            graph.getNodes().forEach(n -> n.setLevel(0));
            graph.getSource().setLevel(0);
            graph.getSink().setLevel(0);
        }
        final Set<FlowNode> toVisit = node.getOutput().stream()
                .filter(e -> e.getFlow() < e.getCapacity() && (e.getTo().getLevel() == 0))
                .map(e -> {
                    final FlowNode n = e.getTo();
                    n.setLevel(node.getLevel() +1);
                    return n;
                })
                .collect(Collectors.toSet());

        toVisit.forEach(n -> n.setLevel(node.getLevel() + 1));
        toVisit.forEach(n -> setNodesLevels(n));
        return graph.getSink().getLevel() > 0;
    }

    private boolean hasSeparatingSubset(final Long k, final List<FlowNode> toCut)
    {
        final Long n = (long) toCut.size();

        if(k > 63){
            System.out.print("Set too big\n");
            return false;
        }
        for (long i = 0; i < (1l<<n); i++)
        {
            if(Long.bitCount(i) == k) {
                final List<Integer> subset = new ArrayList<>();
                for (int j = 0; j < n; j++)
                    if ((i & (1 << j)) > 0)
                        subset.add(j);

                if(!canConnectSource(subset, toCut)) {
                    separatingNodes.clear();
                    separatingNodes.addAll(subset.stream().map(id -> toCut.get(id).getId()).collect(Collectors.toSet()));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canConnectSource(final List<Integer> subset, final List<FlowNode> toCut) {
        graph.getEdges().forEach(e -> e.setFlow(0));
        subset.forEach(i -> {
            final FlowNode n = toCut.get(i);
            n.getOutput().forEach(e -> e.setFlow(Integer.MAX_VALUE));
        });
        return setNodesLevels(graph.getSource());
    }


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
}
