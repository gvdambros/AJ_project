package model.graph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gvdambros on 11/19/16.
 */

public class MyGraph {

    private ObservableList<MyEdge> edges;
    private ObservableList<MyNode> nodes;
    private ObservableList<MyRibbon> ribbons;
    private ObservableList<MyEdge> helixes;

    public MyGraph() {
        edges = FXCollections.observableList(new ArrayList<>());
        nodes = FXCollections.observableList(new ArrayList<>());
        ribbons = FXCollections.observableList(new ArrayList<>());
        helixes = FXCollections.observableList(new ArrayList<>());
    }

    public void addNode( MyNode newNode ){ nodes.add(newNode);}

    public void removeNode( MyNode oldNode ){
        for(MyEdge temp: oldNode.getOutEdges()){
            temp.getTarget().removeInEdge( temp );
            edges.remove(temp);
        }
        for(MyEdge temp: oldNode.getInEdges()){
            temp.getSource().removeOutEdge( temp );
            edges.remove(temp);
        }
        nodes.remove(oldNode);
    }

    public void connectNodes(MyNode source, MyNode target ) throws InvalidEdge {
        addEdge( new MyEdge(source, target));
    }

    public void connectNodes(MyNode source, MyNode target, String name ) throws InvalidEdge {
        addEdge( new MyEdge(source, target, name));
    }

    public void connectNodes(MyNode source, MyNode target, String name, double weight ) throws InvalidEdge {
        addEdge( new MyEdge(source, target, name, weight));
    }

    private void addEdge(MyEdge myEdge) throws InvalidEdge {
        if( isValidEdge( myEdge ) ) {
            if( !nodes.contains( myEdge.getTarget() ) ){
                nodes.add( myEdge.getTarget() );
            }
            if( !nodes.contains( myEdge.getSource() ) ){
                nodes.add( myEdge.getSource() );
            }
            myEdge.getTarget().addInEdge(myEdge);
            myEdge.getSource().addOutEdge(myEdge);
            edges.add(myEdge);
        }
    }

    public void removeEdge(MyEdge myEdge){
        edges.remove(myEdge);
        myEdge.getSource().removeOutEdge(myEdge);
        myEdge.getTarget().removeInEdge(myEdge);
    }

    private boolean isValidEdge(MyEdge myEdge) throws InvalidEdge {
        if( isParallelEdge(myEdge.getSource(), myEdge.getTarget()) ) throw new InvalidEdge("The graph cannot have parallel edges.");
        if( isLoopEdge( myEdge.getSource(), myEdge.getTarget()) ) throw new InvalidEdge("The graph cannot have loop edges.");
        return true;
    }

    private boolean isParallelEdge(MyNode source, MyNode target){
        for(MyEdge temp: edges){
            if( temp.isEqual(source, target) ){
                return true;
            }
        }
        return false;
    }

    private boolean isLoopEdge(MyNode source, MyNode target){
        if( source == target ){
            return true;
        }
        return false;
    }

    public int numberOfEdges(){
        return edges.size();
    }

    public int numberOfNodes(){
        return nodes.size();
    }

    public int numberOfRibbons(){ return ribbons.size(); }

    public ObservableList<MyEdge> getObservableEdges() {
        return edges;
    }

    public ObservableList<MyNode> getObservableNodes() {
        return nodes;
    }

    public ObservableList<MyRibbon> getObservableRibbons() {
        return ribbons;
    }

    public ObservableList<MyEdge> getObservableHelixes() { return helixes; }

    public void clear() {
        edges.clear();
        nodes.clear();
        ribbons.clear();
        helixes.clear();
    }

    public List<MyNode> getNodesByResidualNumber(int residualNumber){
        List<MyNode> myNodeList = new ArrayList<>();
        for(MyNode temp: nodes){
            if(temp.getResidualNumber() == residualNumber){
                myNodeList.add(temp);
            }
        }
        return myNodeList;
    }

    public void addRibbon(MyRibbon myRibbon) {
        ribbons.add(myRibbon);
    }

    public void addHelix(MyNode source, MyNode target) throws InvalidEdge {
        MyEdge myEdge = new MyEdge(source, target);
        if( isValidEdge( myEdge ) ) {
            myEdge.getTarget().addInEdge(myEdge);
            myEdge.getSource().addOutEdge(myEdge);
            helixes.add(myEdge);
        }
    }

}

