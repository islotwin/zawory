package algorithms;


import flowNetwork.FlowNetwork;
import flowNetwork.FlowNode;
import graph.NodeTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

public class BF extends Algorithm {
    private final List<FlowNode> valves;

    public BF(final FlowNetwork graph){
        super(graph);
        this.valves = graph.getNodes().stream().filter(n -> n.getType() == NodeTypeEnum.V).collect(Collectors.toList());
    }

    public Set<Integer> run(){
        if(!isSourceConnected(Collections.emptySet()))
            return Collections.emptySet();
        Long k = (long) valves.size() - 1;
        while(k > 0 && hasSeparatingSubset(k, valves))
            k--;

        return separatingNodes;
    }

    public boolean isSourceConnected(final Set<Integer> subset){
    resetFlow();
        valves.stream()
                .filter(n -> subset.contains(n.getId()))
                .map(n -> n.getOutput())
                .flatMap(s -> s.stream())
                .forEach(e -> e.setFlow(Integer.MAX_VALUE));
        return setNodesLevels(graph.getSource());
    }

}
