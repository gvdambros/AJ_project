package graph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * Created by gvdambros on 11/19/16.
 */

public class MyGraph {

    private ObservableList<MyEdge> edges;
    private ObservableList<MyNode> nodes;


    public MyGraph() {
        edges = FXCollections.observableList(new ArrayList<>());
        nodes = FXCollections.observableList(new ArrayList<>());
    }

    public void addNode( MyNode newNode ){
        nodes.add(newNode);
    }


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

    public void disconnectNodes(MyNode source, MyNode target){

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

    public int numberOfConnectedComponents(){

        List<ConnectedComponent> connectedComponents = new ArrayList<>();

        for(MyNode temp: nodes) {
            connectedComponents.add( new ConnectedComponent(temp) );
        }

        for(int i = 0; i < connectedComponents.size(); i++){
            ConnectedComponent tempCC = connectedComponents.get(i);
            for(MyEdge tempEge: tempCC.getNodes().get(0).getOutEdges()) {
                if (!tempCC.hasNode( tempEge.getTarget() )) {
                    for (int j = 0; j < connectedComponents.size(); j++){
                        ConnectedComponent tempCC2 = connectedComponents.get(j);
                        if(tempCC2.hasNode(tempEge.getTarget())){
                            tempCC.join(tempCC2);
                            connectedComponents.remove(tempCC2);
                            if(j < i) i--;
                            break;
                        }
                    }
                }
            }
        }

        return connectedComponents.size();
    }

    public void write(String file) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        Hashtable<MyNode, Integer> nodeToID = new Hashtable<>();
        for(int i = 0; i < this.numberOfNodes(); i++){
            bw.write((i + 1) + " " + nodes.get(i) + "\n" );
            nodeToID.put(nodes.get(i), i+1);
        }
        bw.write("#\n");
        for(MyEdge temp: edges){
            bw.write(nodeToID.get( temp.getSource() ) + " " + nodeToID.get( temp.getTarget() ) + " " + temp.getName() + "\n" );
        }
        bw.flush();
        bw.close();
    }

    public ObservableList<MyEdge> getObservableEdges() {
        return edges;
    }

    public ObservableList<MyNode> getObservableNodes() {
        return nodes;
    }

    public void clear() {
        edges.clear();
        nodes.clear();
    }
}

