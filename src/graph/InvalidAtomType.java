package graph;

/**
 * Created by gvdambros on 1/20/17.
 */
public class InvalidAtomType extends Exception {
    public InvalidAtomType() {
    }

    public InvalidAtomType(String message) {
        super(message);
    }
}
