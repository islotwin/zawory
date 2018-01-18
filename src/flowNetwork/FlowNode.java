package flowNetwork;

import graph.Node;
import graph.NodeTypeEnum;

import java.util.HashSet;
import java.util.Set;

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

    public void setLevel(Integer level){
        this.level = level;
    }

    public Set<FlowEdge> getInput(){
        return input;
    }

    public Set<FlowEdge> getOutput(){
        return output;
    }

    public Integer getId(){
        return id;
    }

    public Integer getLevel(){
        return level;
    }

    public NodeTypeEnum getType(){
        return type;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof FlowNode)) {
            return false;
        }

        FlowNode node = (FlowNode) o;

        return id.equals(node.getId());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        result = 31 * result + level;
        result = 31 * result + type.hashCode();

        return result;
    }

    public static class STFlowNode extends FlowNode{
        private final Set<Node> nodes;

        public STFlowNode(final Integer id, final NodeTypeEnum type){
            super(id, 0, type);
            this.nodes = new HashSet<>();
        }

        public Set<Node> getNodes(){
            return nodes;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        }
}