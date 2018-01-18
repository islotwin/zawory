package graph;

public class Edge {
    private final Node from;
    private final Node to;

    public Edge(final Node from, final Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getFrom(){
        return from;
    }

    public Node getTo(){
        return to;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Edge)) {
            return false;
        }

        Edge edge = (Edge) o;

        return from.equals(edge.getFrom()) && to.equals(edge.getTo());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

}
