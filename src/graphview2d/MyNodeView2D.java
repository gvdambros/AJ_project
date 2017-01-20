package graphview2d;

import graph.MyNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by gvdambros on 11/25/16.
 */
public class MyNodeView2D extends Group{

    private MyNode myNode;
    private DoubleProperty xPosition, yPosition;
    public Circle circle;

    static private int id = 1;

    public MyNodeView2D(MyNode myNode) {
        xPosition = new SimpleDoubleProperty(id*50);
        yPosition = new SimpleDoubleProperty(id*50);
        this.myNode = myNode;
        circle = new Circle(0, 0, 20, Color.color(Math.random(), Math.random(), Math.random()));
        id++;
        this.getChildren().add(circle);
        setBindings();
    }



    public MyNodeView2D(MyNode myNode, double x, double y) {
        xPosition = new SimpleDoubleProperty(x);
        yPosition = new SimpleDoubleProperty(y);
        this.myNode = myNode;
        Circle drawing = new Circle(0, 0, 20, Color.color(Math.random(), Math.random(), Math.random()));

        this.getChildren().add(drawing);
        setBindings();
    }

    private void setBindings() {
        this.translateXProperty().bind(xPosition);
        this.translateYProperty().bind(yPosition);
    }

    public MyNode getMyNode() {
        return myNode;
    }

    public DoubleProperty xPositionProperty() {
        return xPosition;
    }

    public DoubleProperty yPositionProperty() {
        return yPosition;
    }

    public double getXPosition() {
        return xPosition.get();
    }

    public double getYPosition() {
        return yPosition.get();
    }
}
