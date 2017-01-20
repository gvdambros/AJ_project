package graph;

/**
 * Created by gvdambros on 1/17/17.
 */
public class Strand {

    public int begin, end;
    public String id;

    public Strand(String id, int begin, int end) {
        this.begin = begin;
        this.end = end;
        this.id = id;
    }

    public String toString(){
        return this.id + " " + this.begin + " " + this.end;
    }
}
