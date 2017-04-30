package model.graph;

import model.sequence.AtomInfo;
import model.sequence.InvalidAtomType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gvdambros on 11/19/16.
 */
public class MyNode {

    private AtomInfo atomInfo;
    private String ID;


    private int residualNumber;

    private ObservableList<MyEdge> outEdges;
    private ObservableList<MyEdge> inEdges;

    public MyNode(String ID, String type, int residualNumber) throws InvalidAtomType {

        this.ID = ID;
        this.atomInfo = new AtomInfo(type);
        this.residualNumber = residualNumber;

        outEdges = FXCollections.observableList(new ArrayList<>());
        inEdges = FXCollections.observableList(new ArrayList<>());
    }

    public String getID() {
        return ID;
    }

    public AtomInfo getAtomInfo() {
        return atomInfo;
    }

    public List<MyEdge> getOutEdges() { return outEdges; }

    public void addOutEdge(MyEdge myEdge){ outEdges.add(myEdge); }

    public void removeOutEdge(MyEdge myEdge){ outEdges.remove(myEdge); }

    public List<MyEdge> getInEdges() { return inEdges; }

    public void addInEdge(MyEdge myEdge){ inEdges.add(myEdge); }

    public void removeInEdge(MyEdge myEdge){ inEdges.remove(myEdge); }

    public String toString(){
        return this.getID();
    }

    public int getResidualNumber() {
        return residualNumber;
    }
}
