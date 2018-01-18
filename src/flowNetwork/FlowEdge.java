package flowNetwork;

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

    public FlowNode getFrom(){
        return from;
    }

    public FlowNode getTo(){
        return to;
    }

    public Integer getCapacity(){
        return capacity;
    }

    public Integer getFlow(){
        return flow;
    }

    public void setFrom(final FlowNode from){
        this.from = from;
    }

    public void setTo(final FlowNode to){
        this.to = to;
    }

    public void setCapacity(final Integer capacity){
        this.capacity = capacity;
    }

    public void setFlow(final Integer flow){
        this.flow = flow;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof FlowEdge)) {
            return false;
        }

        FlowEdge edge = (FlowEdge) o;

        return from.equals(edge.getFrom()) && to.equals(edge.getTo());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + from.hashCode();
        result = 31 * result + capacity;
        result = 31 * result + flow;
        result = 31 * result + to.hashCode();
        return result;
    }

}
