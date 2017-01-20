package graph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by gvdambros on 1/17/17.
 */
public class Sheet {

    public ObservableList<Strand> strands;
    public ObservableList<Integer> relativeDirection;

    public Sheet() {
        strands = FXCollections.observableList(new ArrayList<>());
        relativeDirection = FXCollections.observableList(new ArrayList<>());
    }

    public String toString(){
        // TODO: 1/17/17
        return "";
    }

    public void addStrand(Strand strand){ strands.add(strand); }

    
}
