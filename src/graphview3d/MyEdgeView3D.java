package graphview3d;

import graph.MyEdge;
import graphview2d.MyNodeView2D;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Created by gvdambros on 12/10/16.
 */
public class MyEdgeView3D extends Group {

    private MyEdge myEdge;
    public MyLine3D line;

    public MyEdgeView3D(MyEdge myEdge, MyNodeView3D source, MyNodeView3D target) {
        this.myEdge = myEdge;

        line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), Color.BLACK );

        source.xPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), Color.BLACK );
            this.getChildren().add(line);
        });

        source.yPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), Color.BLACK );
            this.getChildren().add(line);
        });

        source.zPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), Color.BLACK );
            this.getChildren().add(line);
        });

        target.xPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), Color.BLACK );
            this.getChildren().add(line);
        });

        target.yPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), Color.BLACK );
            this.getChildren().add(line);
        });

        target.zPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), Color.BLACK );
            this.getChildren().add(line);
        });

        this.getChildren().add(line);
    }

    public MyEdge getMyEdge() { return myEdge; }
}
