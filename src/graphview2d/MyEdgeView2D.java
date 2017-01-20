package graphview2d;

import graph.MyEdge;
import javafx.scene.Group;
import javafx.scene.shape.Line;

/**
 * Created by gvdambros on 11/25/16.
 */
public class MyEdgeView2D extends Group {;


    MyEdge myEdge;
    public Line line;

    public MyEdgeView2D(MyEdge myEdge, MyNodeView2D source, MyNodeView2D target) {
        this.myEdge = myEdge;

        line = new Line();
        line.setStrokeWidth(10);


        line.startXProperty().bind( source.translateXProperty() );
        line.startYProperty().bind( source.translateYProperty() );

        line.endXProperty().bind( target.translateXProperty() );
        line.endYProperty().bind( target.translateYProperty() );

        this.getChildren().add(line);
    }

    public MyEdge getMyEdge() {
        return myEdge;
    }
}
