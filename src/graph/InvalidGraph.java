package graph;

/**
 * Created by gvdambros on 11/19/16.
 */
public class InvalidGraph extends Exception {

    public InvalidGraph() {
    }

    public InvalidGraph(String message) {
        super(message);
    }
}
