package algorithms;

import flowNetwork.FlowNetwork;
import flowNetwork.FlowNode;
import graph.NodeTypeEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Algorithm {
    final FlowNetwork graph;
    final Set<Integer> separatingNodes;

    Algorithm(final FlowNetwork graph){
        this.graph = graph;
        this.separatingNodes = new HashSet<>();
    }

    abstract public Set<Integer> run();

    //set nodes levels and simultaneously check wheter source is connected with sink
    boolean setNodesLevels(final FlowNode node){
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

    void resetFlow(){
        graph.getEdges().forEach(e -> e.setFlow(0));
    }

    boolean hasSeparatingSubset(final Long k, final List<FlowNode> toRemove)
    {
        final Long n = (long) toRemove.size();

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

                final Set<Integer> nodesIdSubset = subset.stream()
                        .map(it -> toRemove.get(it).getId())
                        .collect(Collectors.toSet());
                if(!isSourceConnected(nodesIdSubset)) {
                    separatingNodes.clear();
                    separatingNodes.addAll(nodesIdSubset);
                    return true;
                }
            }
        }
        return false;
    }

    abstract public boolean isSourceConnected(final Set<Integer> toRemove);
}
