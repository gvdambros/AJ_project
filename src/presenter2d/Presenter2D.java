package presenter2d;

import J4BioInfWS2016.graphview2d.SpringEmbedder;
import graphview2d.*;
import graph.InvalidEdge;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyNode;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Hashtable;

import static J4BioInfWS2016.graphview2d.SpringEmbedder.computeSpringEmbedding;


/**
 * Created by gvdambros on 11/26/16.
 */
public class Presenter2D {

    private MyGraph myGraph;
    private ObjectProperty<MyNode> nodeToBeDeleted;
    private ObjectProperty<MyEdge> edgeToBeDeleted;
    private MyNode nodeToBeConnected = null;
    public DoubleProperty clickedPositionX;
    public DoubleProperty clickedPositionY;
    public double draggedPositionX;
    public double draggedPositionY;
    public ObjectProperty<MyExceptionView2D> exceptionProperty;

    public Presenter2D(MyGraph myGraph) {

        this.myGraph = myGraph;
        nodeToBeDeleted = new SimpleObjectProperty<>();
        edgeToBeDeleted = new SimpleObjectProperty<>();

        clickedPositionX = new SimpleDoubleProperty();
        clickedPositionY = new SimpleDoubleProperty();

        draggedPositionX = 0;
        draggedPositionY = 0;

        exceptionProperty = new SimpleObjectProperty<>();

        setBindings();

    }

    private void setBindings() {

        nodeToBeDeleted.addListener((observable, oldValue, newValue) -> {
            myGraph.removeNode(newValue);
        });

        edgeToBeDeleted.addListener((observable, oldValue, newValue) -> {
            myGraph.removeEdge(newValue);
        });

    }

    public void setNodeBindings(MyNodeView2D myNodeView2D){
        myNodeView2D.setOnMouseClicked(event -> {


            if(event.isShiftDown()){
                if( nodeToBeConnected == null){
                    nodeToBeConnected = myNodeView2D.getMyNode();
                }
                else{
                    try {
                        myGraph.connectNodes(nodeToBeConnected, myNodeView2D.getMyNode());
                    } catch (InvalidEdge invalidEdge) {
                        exceptionProperty.setValue( new MyExceptionView2D(invalidEdge.getMessage()));
                    }
                    finally {
                        nodeToBeConnected = null;
                    }
                }
            } else if (event.getClickCount() == 2) {
                nodeToBeConnected = null;
                nodeToBeDeleted.setValue( myNodeView2D.getMyNode() );
            }
            event.consume();
        });

        myNodeView2D.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(200));
            scaleTransition.setNode(myNodeView2D);
            scaleTransition.setToX(3);
            scaleTransition.setToY(3);
            scaleTransition.play();
        });

        myNodeView2D.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(200));
            scaleTransition.setNode(myNodeView2D);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        });

        myNodeView2D.setOnMouseReleased(event -> {
            draggedPositionX = 0;
            draggedPositionY = 0;
        });

        myNodeView2D.setOnMousePressed(event -> {
            draggedPositionX = event.getSceneX();
            draggedPositionY = event.getSceneY();
        });

        myNodeView2D.setOnMouseDragged(event -> {

            System.out.println("Dragged position: " + draggedPositionX + " " + draggedPositionY);

            double deltaX = event.getSceneX() - draggedPositionX;
            double deltaY = event.getSceneY() - draggedPositionY;

            System.out.println("Delta: " + deltaX + " " + deltaY);


            myNodeView2D.xPositionProperty().set( myNodeView2D.getXPosition() + deltaX );
            myNodeView2D.yPositionProperty().set( myNodeView2D.getYPosition() + deltaY );

            draggedPositionX = event.getSceneX();
            draggedPositionY = event.getSceneY();

            nodeToBeConnected = null;
            event.consume();
        });
    }

    public void setEdgeBindings(MyEdgeView2D myEdgeView2D){
        myEdgeView2D.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1){
                System.out.println("run");
                Circle circle = new Circle(10, Color.YELLOW);

                myEdgeView2D.getChildren().add(circle);

                PathTransition tt = new PathTransition();

                tt.setNode(circle);
                tt.setDuration(Duration.millis(200));
                tt.setPath(myEdgeView2D.line);
                tt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                tt.setCycleCount(20);
                tt.setOnFinished(ended ->{
                    myEdgeView2D.getChildren().remove(circle);
                });

                tt.play();
            }
            if (event.getClickCount() == 2) {
                edgeToBeDeleted.setValue( myEdgeView2D.getMyEdge() );
                nodeToBeConnected = null;
                event.consume();
            }
        });
    }

    public void setCenterPaneBindings(Pane pane){

    }

    public void setViewerBindings(SimpleGraphViewer simpleGraphViewer) {
        simpleGraphViewer.embedding.setOnAction(event -> {
            simpleGraphViewer.embedding.setDisable(true);

            Hashtable<MyNode, Integer> nodeToID = new Hashtable<MyNode, Integer>();

            int numberOfNodes = myGraph.numberOfNodes();

            double initialCoordinates[][] = new double[ myGraph.numberOfNodes() ][2];
            double finalCoordinates[][] = new double[ myGraph.numberOfNodes() ][2];

            int[][] edges = new int[ myGraph.numberOfEdges() ][2];

            int id = 0;
            for(Node temp: simpleGraphViewer.myGraphView2D.nodeViewGroup.getChildren()){
                initialCoordinates[id][0] = ((MyNodeView2D) temp).getTranslateX();
                initialCoordinates[id][1] = ((MyNodeView2D) temp).getTranslateY();
                MyNode myNode = ((MyNodeView2D) temp).getMyNode();
                nodeToID.put( myNode, id++ );
            }

            int i = 0;
            for( Node temp: simpleGraphViewer.myGraphView2D.edgeViewGroup.getChildren() ){
                MyEdge myEdge = ((MyEdgeView2D) temp).getMyEdge();
                edges[i][0] = nodeToID.get( myEdge.getSource() );
                edges[i++][1] = nodeToID.get( myEdge.getTarget() );
            }

            finalCoordinates = computeSpringEmbedding(100, numberOfNodes, edges, initialCoordinates);

            SpringEmbedder.centerCoordinates(finalCoordinates, 10, (int) simpleGraphViewer.centerPane.getWidth() - 10, 10, (int) simpleGraphViewer.centerPane.getHeight() - 10);

            KeyValue xyValues[] = new KeyValue[2*numberOfNodes];

            i = 0;
            for(Node temp: simpleGraphViewer.myGraphView2D.nodeViewGroup.getChildren()){
                xyValues[2*i] = new KeyValue( ((MyNodeView2D) temp).xPositionProperty(),  finalCoordinates[i][0]);
                xyValues[2*i + 1] = new KeyValue( ((MyNodeView2D) temp).yPositionProperty(),  finalCoordinates[i++][1]);
            }

            KeyFrame keyFrame;
            keyFrame = new KeyFrame(Duration.millis(1000),xyValues );

            Timeline timeline = new Timeline(keyFrame);

            timeline.setOnFinished(ended -> {
                simpleGraphViewer.embedding.setDisable(false);
            });

            timeline.play();

        });

        simpleGraphViewer.clear.setOnAction(event -> {
            simpleGraphViewer.clear.setDisable(true);

            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setToX(0);
            scaleTransition.setToY(0);
            scaleTransition.setDuration(Duration.seconds(1));

            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setByValue(0);
            fadeTransition.setDuration(Duration.seconds(1));

            ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
            parallelTransition.setNode( simpleGraphViewer.myGraphView2D );

            parallelTransition.setOnFinished(ended -> {
                simpleGraphViewer.myGraphView2D.edgeViewGroup.getChildren().clear();
                simpleGraphViewer.myGraphView2D.nodeViewGroup.getChildren().clear();
                simpleGraphViewer.myGraphView2D.opacityProperty().setValue(1);
                simpleGraphViewer.myGraphView2D.scaleXProperty().setValue(1);
                simpleGraphViewer.myGraphView2D.scaleYProperty().setValue(1);
                myGraph.clear();
                simpleGraphViewer.clear.setDisable(false);
            });


            parallelTransition.play();
        });
    }
}
