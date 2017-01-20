package graphview3d;

import presenter3d.Presenter3D;
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
import javafx.scene.control.Alert;

import java.io.IOException;

/**
 * Created by gvdambros on 12/10/16.
 */
public class MyGraphView3D extends Group{

    public Group edgeViewGroup;
    public Group nodeViewGroup;

    private DoubleProperty clickedPositionX;
    private DoubleProperty clickedPositionY;
    private DoubleProperty clickedPositionZ;
    ObjectProperty<MyExceptionView3D> exceptionProperty;

    public MyGraphView3D(MyGraph myGraph, Presenter3D presenter3D) {

        edgeViewGroup = new Group();
        this.getChildren().add(edgeViewGroup);

        nodeViewGroup = new Group();
        this.getChildren().add(nodeViewGroup);

        clickedPositionX = new SimpleDoubleProperty();
        clickedPositionY = new SimpleDoubleProperty();
        clickedPositionZ = new SimpleDoubleProperty();

        exceptionProperty = new SimpleObjectProperty<>();

        init(myGraph, presenter3D);
        setBindings(myGraph, presenter3D);

    }

    private void init(MyGraph myGraph, Presenter3D presenter3D) {
        for (MyNode temp: myGraph.getObservableNodes()){
            addMyNodeView3D(temp, presenter3D);

        }
        for (MyEdge temp: myGraph.getObservableEdges()){
            try {
                addMyEdgeView3D(temp, presenter3D);
            } catch (MyExceptionView3D myExceptionView3D) {
                MyAlertView3D myAlertView3D = new MyAlertView3D(Alert.AlertType.ERROR, myExceptionView3D.getMessage());
                myAlertView3D.showAndWait();
            }
        }
    }

    private void setBindings(MyGraph myGraph, Presenter3D presenter3D) {

        clickedPositionX.bind( presenter3D.clickedPositionX );
        clickedPositionY.bind( presenter3D.clickedPositionY );
        clickedPositionZ.bind( presenter3D.clickedPositionZ );

        exceptionProperty.bind( presenter3D.exceptionProperty );

        exceptionProperty.addListener((observable, oldValue, newValue) -> {
            MyAlertView3D myAlertView3D = new MyAlertView3D(Alert.AlertType.ERROR, exceptionProperty.getValue().getMessage() );
            myAlertView3D.showAndWait();
        });

        myGraph.getObservableEdges().addListener(new ListChangeListener<MyEdge>() {
            @Override
            public void onChanged(Change<? extends MyEdge> c) {
                while( c.next() ){
                    try{
                        if(c.wasAdded()){
                            for (MyEdge temp: c.getAddedSubList()) {
                                addMyEdgeView3D(temp, presenter3D);
                            }
                        }
                        else if(c.wasRemoved()){
                            for(MyEdge temp: c.getRemoved()){
                                deleteMyEdgeView3D( getMyEdgeView3D(temp) );
                            }
                        }
                    } catch (MyExceptionView3D e) {
                        MyAlertView3D myAlertView3D = new MyAlertView3D(Alert.AlertType.ERROR, e.getMessage());
                        myAlertView3D.showAndWait();
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
                                addMyNodeView3D(temp, presenter3D);
                            }

                        } else if (c.wasRemoved()) {
                            for (MyNode temp : c.getRemoved()) {
                                deleteMyNodeView3D(getMyNodeView3D(temp));
                            }
                        }

                    } catch (MyExceptionView3D e) {
                        MyAlertView3D myAlertView3D = new MyAlertView3D(Alert.AlertType.ERROR, e.getMessage());
                        myAlertView3D.showAndWait();
                    }
                }
            }
        });


    }

    private MyNodeView3D getMyNodeView3D(MyNode myNode) throws MyExceptionView3D {
        for(Node temp: nodeViewGroup.getChildren()){
            if(temp instanceof MyNodeView3D){
                if( ((MyNodeView3D) temp).getMyNode().equals(myNode)){
                    return (MyNodeView3D) temp;
                }
            }
        }
        throw new MyExceptionView3D("This node is not in present in the Graph.");
    }

    private MyEdgeView3D getMyEdgeView3D(MyEdge myEdge) throws MyExceptionView3D {
        for(Node temp: edgeViewGroup.getChildren()){
            if(temp instanceof MyEdgeView3D){
                if( ((MyEdgeView3D) temp).getMyEdge().equals(myEdge)){
                    return (MyEdgeView3D) temp;
                }
            }
        }
        throw new MyExceptionView3D("This edge is not in present in the Graph.");
    }

    public void addMyNodeView3D(MyNode myNode, Presenter3D presenter3D){
        MyNodeView3D myNodeView3D = new MyNodeView3D(myNode, presenter3D.atomsPosition.get(myNode.getID()) );
        nodeViewGroup.getChildren().add(myNodeView3D);
        presenter3D.setNodeBindings(myNodeView3D);
    }

    public void addMyEdgeView3D(MyEdge myEdge, Presenter3D presenter3D) throws MyExceptionView3D {
        MyNodeView3D sourceView3D = getMyNodeView3D( myEdge.getSource() );
        MyNodeView3D targetView3D = getMyNodeView3D( myEdge.getTarget() );
        MyEdgeView3D myEdgeView3D = new MyEdgeView3D(myEdge, sourceView3D, targetView3D);
        edgeViewGroup.getChildren().add(myEdgeView3D);
        presenter3D.setEdgeBindings(myEdgeView3D);
    }

    private void deleteMyNodeView3D(MyNodeView3D myNodeView3D) {
        nodeViewGroup.getChildren().remove(myNodeView3D);
    }

    private void deleteMyEdgeView3D(MyEdgeView3D myEdgeView3D) {
        edgeViewGroup.getChildren().remove(myEdgeView3D);
    }

}
