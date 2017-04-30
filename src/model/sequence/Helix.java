package model.sequence;

/**
 * Created by gvdambros on 1/14/17.
 */
public class Helix {

    public int begin, end;
    public String id;

    public Helix(String id, int begin, int end) {
        this.begin = begin;
        this.end = end;
        this.id = id;
    }

    public String toString(){
        return this.id + " " + this.begin + " " + this.end;
    }

}
