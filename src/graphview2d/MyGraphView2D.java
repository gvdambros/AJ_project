package graphview2d;

import presenter2d.Presenter2D;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;

/**
 * Created by gvdambros on 11/25/16.
 */
public class MyGraphView2D extends Group{

    public Group edgeViewGroup;
    public Group nodeViewGroup;

    private DoubleProperty clickedPositionX;
    private DoubleProperty clickedPositionY;
    ObjectProperty<MyExceptionView2D> exceptionProperty;

    public MyGraphView2D(MyGraph myGraph, Presenter2D presenter2D) {

        edgeViewGroup = new Group();
        this.getChildren().add(edgeViewGroup);

        nodeViewGroup = new Group();
        this.getChildren().add(nodeViewGroup);

        clickedPositionX = new SimpleDoubleProperty();
        clickedPositionY = new SimpleDoubleProperty();

        exceptionProperty = new SimpleObjectProperty<>();

        init(myGraph, presenter2D);
        setBindings(myGraph, presenter2D);

    }

    private void init(MyGraph myGraph, Presenter2D presenter2D) {
        for (MyNode temp: myGraph.getObservableNodes()){
            addMyNodeView2D(temp, presenter2D);

        }
        for (MyEdge temp: myGraph.getObservableEdges()){
            try {
                addMyEdgeView2D(temp, presenter2D);
            } catch (MyExceptionView2D myExceptionView2D) {
                MyAlertView2D myAlertView2D = new MyAlertView2D(AlertType.ERROR, myExceptionView2D.getMessage());
                myAlertView2D.showAndWait();
            }
        }
    }

    private void setBindings(MyGraph myGraph, Presenter2D presenter2D) {

        clickedPositionX.bind( presenter2D.clickedPositionX );
        clickedPositionY.bind( presenter2D.clickedPositionY );
        exceptionProperty.bind( presenter2D.exceptionProperty );

        exceptionProperty.addListener((observable, oldValue, newValue) -> {
            MyAlertView2D myAlertView2D = new MyAlertView2D(AlertType.ERROR, exceptionProperty.getValue().getMessage() );
            myAlertView2D.showAndWait();
        });

        myGraph.getObservableEdges().addListener(new ListChangeListener<MyEdge>() {
            @Override
            public void onChanged(Change<? extends MyEdge> c) {
                while( c.next() ){
                    try{
                        if(c.wasAdded()){
                            for (MyEdge temp: c.getAddedSubList()) {
                                addMyEdgeView2D(temp, presenter2D);
                            }
                        }
                        else if(c.wasRemoved()){
                            for(MyEdge temp: c.getRemoved()){
                                deleteMyEdgeView2D( getMyEdgeView2D(temp) );
                            }
                        }
                    } catch (MyExceptionView2D e) {
                        MyAlertView2D myAlertView2D = new MyAlertView2D(AlertType.ERROR, e.getMessage());
                        myAlertView2D.showAndWait();
                    }
                }
            }
        });

        myGraph.getObservableNodes().addListener(new ListChangeListener<MyNode>() {
            @Override
            public void onChanged(Change<? extends MyNode> c) {
                while ( c.next() ) {
                    try {
                        if (c.wasAdded()) {
                            for (MyNode temp : c.getAddedSubList()) {
                                addMyNodeView2D(temp, clickedPositionX.getValue(), clickedPositionY.getValue(), presenter2D);
                            }

                        } else if (c.wasRemoved()) {
                            for (MyNode temp : c.getRemoved()) {
                                deleteMyNodeView2D(getMyNodeView2D(temp));
                            }
                        }

                    } catch (MyExceptionView2D e) {
                        MyAlertView2D myAlertView2D = new MyAlertView2D(AlertType.ERROR, e.getMessage());
                        myAlertView2D.showAndWait();
                    }
                }
            }
        });


    }

    private MyNodeView2D getMyNodeView2D(MyNode myNode) throws MyExceptionView2D {
        for(Node temp: nodeViewGroup.getChildren()){
            if(temp instanceof MyNodeView2D){
                if( ((MyNodeView2D) temp).getMyNode().equals(myNode)){
                    return (MyNodeView2D) temp;
                }
            }
        }
        throw new MyExceptionView2D("This node is not in present in the Graph.");
    }

    private MyEdgeView2D getMyEdgeView2D(MyEdge myEdge) throws MyExceptionView2D {
        for(Node temp: edgeViewGroup.getChildren()){
            if(temp instanceof MyEdgeView2D){
                if( ((MyEdgeView2D) temp).getMyEdge().equals(myEdge)){
                    return (MyEdgeView2D) temp;
                }
            }
        }
        throw new MyExceptionView2D("This edge is not in present in the Graph.");
    }

    public void addMyNodeView2D(MyNode myNode, Presenter2D presenter2D){
        MyNodeView2D myNodeView2D = new MyNodeView2D(myNode);
        nodeViewGroup.getChildren().add(myNodeView2D);
        presenter2D.setNodeBindings(myNodeView2D);
    }

    public void addMyNodeView2D(MyNode myNode, double x, double y, Presenter2D presenter2D){
        MyNodeView2D myNodeView2D = new MyNodeView2D(myNode, x, y);
        nodeViewGroup.getChildren().add(myNodeView2D);
        presenter2D.setNodeBindings(myNodeView2D);
    }

    public void addMyEdgeView2D(MyEdge myEdge, Presenter2D presenter2D) throws MyExceptionView2D {
        MyNodeView2D sourceView2D = null;
        MyNodeView2D targetView2D = null;

        sourceView2D = getMyNodeView2D( myEdge.getSource() );
        targetView2D = getMyNodeView2D( myEdge.getTarget() );

        MyEdgeView2D myEdgeView2D = new MyEdgeView2D(myEdge, sourceView2D, targetView2D);
        edgeViewGroup.getChildren().add(myEdgeView2D);
        presenter2D.setEdgeBindings(myEdgeView2D);
    }

    private void deleteMyNodeView2D(MyNodeView2D myNodeView2D) {
        nodeViewGroup.getChildren().remove(myNodeView2D);
    }

    private void deleteMyEdgeView2D(MyEdgeView2D myEdgeView2D) {
        edgeViewGroup.getChildren().remove(myEdgeView2D);
    }


}