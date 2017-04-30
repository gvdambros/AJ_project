package model.graph;

/**
 * Created by gvdambros on 11/19/16.
 */
public class InvalidEdge extends Exception {

    public InvalidEdge() {
    }

    public InvalidEdge(String message) {
        super(message);
    }
}
