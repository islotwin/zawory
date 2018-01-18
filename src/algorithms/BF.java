package algorithms;


import flowNetwork.FlowNetwork;
import flowNetwork.FlowNode;
import graph.NodeTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

public class BF {
    private final FlowNetwork graph;
    private final Set<Integer> separatingNodes;
    private final List<FlowNode> valves;

    public BF(final FlowNetwork graph){
        this.graph = graph;
        this.separatingNodes = new HashSet<>();
        this.valves = graph.getNodes().stream().filter(n -> n.getType() == NodeTypeEnum.V).collect(Collectors.toList());
    }

    public Set<Integer> run(){
        if(!canConnectSource(Collections.emptyList()))
            return Collections.emptySet();
        Long k = graph.countValves() - 1;
        while(k > 0 && hasSeparatingSubset(k))
            k--;

        return separatingNodes;
    }

    private boolean hasSeparatingSubset(final Long k)
    {
        final Long n = (long) valves.size();

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

                if(!canConnectSource(subset)) {
                    separatingNodes.clear();
                    separatingNodes.addAll(subset.stream().map(id -> valves.get(id).getId()).collect(Collectors.toSet()));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canConnectSource(final List<Integer> subset) {
        graph.getEdges().forEach(e -> e.setFlow(0));
        subset.forEach(i -> {
            final FlowNode n = valves.get(i);
            n.getOutput().forEach(e -> e.setFlow(Integer.MAX_VALUE));
        });
        return setNodesLevels(graph.getSource());
    }

    public boolean isSourceConnected(final Set<Integer> subset){
        graph.getEdges().forEach(e -> e.setFlow(0));

        valves.stream()
                .filter(n -> subset.contains(n.getId()))
                .map(n -> n.getOutput())
                .flatMap(s -> s.stream())
                .forEach(e -> e.setFlow(Integer.MAX_VALUE));
        return setNodesLevels(graph.getSource());
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
}
