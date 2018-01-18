package flowNetwork;

import graph.Node;
import graph.NodeTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class FlowNode {
    private final Set<FlowEdge> input;
    private final Set<FlowEdge> output;
    private final Integer id;
    private Integer level;
    private final NodeTypeEnum type;

    public FlowNode(final Integer id, final Integer level, final NodeTypeEnum type){
        this.id = id;
        this.level = level;
        this.type = type;
        this.input = new HashSet<>();
        this.output = new HashSet<>();
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class STFlowNode extends FlowNode{
        private final Set<Node> nodes;

        public STFlowNode(final Integer id, final NodeTypeEnum type){
            super(id, 0, type);
            this.nodes = new HashSet<>();
        }
    }
}