package flowNetwork;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(of = {"from", "to"})
public class FlowEdge {
    private FlowNode from;
    private FlowNode to;
    private Integer capacity;
    private Integer flow;

    public FlowEdge(final FlowNode from, final FlowNode to, final Integer capacity){
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }
}
