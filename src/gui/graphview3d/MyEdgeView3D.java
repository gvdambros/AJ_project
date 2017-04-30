package gui.graphview3d;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import model.graph.MyEdge;
import javafx.scene.Group;
import javafx.scene.paint.Color;

/**
 * Created by gvdambros on 12/10/16.
 */
public class MyEdgeView3D extends Group {

    private MyEdge myEdge;
    public MyLine3D line;

    public FloatProperty radiusProperty;

    public MyEdgeView3D(MyEdge myEdge, MyNodeView3D source, MyNodeView3D target, float radius, Color color) {
        this.myEdge = myEdge;
        radiusProperty = new SimpleFloatProperty(radius);
        line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), radius, color );

        source.xPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), radius, Color.BLACK );
            this.getChildren().add(line);
        });

        source.yPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), radius, Color.BLACK );
            this.getChildren().add(line);
        });

        source.zPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), radius, Color.BLACK );
            this.getChildren().add(line);
        });

        target.xPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), radius, Color.BLACK );
            this.getChildren().add(line);
        });

        target.yPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), radius, Color.BLACK );
            this.getChildren().add(line);
        });

        target.zPositionProperty().addListener(observable -> {
            this.getChildren().remove(line);
            line = new MyLine3D(source.xPositionProperty(), source.yPositionProperty(), source.zPositionProperty(),
                    target.xPositionProperty(), target.yPositionProperty(), target.zPositionProperty(), radius, Color.BLACK );
            this.getChildren().add(line);
        });

        this.getChildren().add(line);

        setBindings();
    }

    private void setBindings() {
        radiusProperty.addListener((observable, oldValue, newValue) -> {
            line.setCylinderRadius((float) newValue);
        });
    }

    public MyEdge getMyEdge() { return myEdge; }
}
