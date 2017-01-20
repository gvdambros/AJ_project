package graphview3d;

import graph.MyNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Sphere;

import java.util.Random;

/**
 * Created by gvdambros on 12/10/16.
 */
public class MyNodeView3D extends Group {

    private MyNode myNode;
    private DoubleProperty xPosition, yPosition, zPosition;
    public Sphere sphere;

    public MyNodeView3D(MyNode myNode, Point3D position) {
        this.myNode = myNode;
        Color color = Color.color( Math.random(), Math.random(), Math.random() );
        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor( color.darker() );
        phongMaterial.setSpecularColor( color.brighter() );

        this.sphere = new Sphere(1);
        sphere.setMaterial(phongMaterial);

        Random rand = new Random();
        xPosition = new SimpleDoubleProperty(position.getX());
        yPosition = new SimpleDoubleProperty(position.getY());
        zPosition = new SimpleDoubleProperty(position.getZ());

        this.getChildren().add(sphere);

        setBindings();
    }

    public MyNodeView3D(MyNode myNode, double x, double y, double z) {
        this.myNode = myNode;
        Color color = Color.color( Math.random(), Math.random(), Math.random() );
        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor( color.darker() );
        phongMaterial.setSpecularColor( color.brighter() );

        this.sphere = new Sphere(12);
        sphere.setMaterial(phongMaterial);

        xPosition = new SimpleDoubleProperty(x);
        yPosition = new SimpleDoubleProperty(y);
        zPosition = new SimpleDoubleProperty(z);

        this.getChildren().add(sphere);

        setBindings();
    }

    private void setBindings() {
        this.sphere.translateXProperty().bind( xPosition );
        this.sphere.translateYProperty().bind( yPosition );
        this.sphere.translateZProperty().bind( zPosition );
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

    public DoubleProperty zPositionProperty() {
        return zPosition;
    }

    public double getXPosition() {
        return xPosition.get();
    }

    public void setXPosition(double xPosition) {
        this.xPosition.set(xPosition);
    }

    public double getYPosition() {
        return yPosition.get();
    }

    public void setYPosition(double yPosition) {
        this.yPosition.set(yPosition);
    }

    public double getZPosition() {
        return zPosition.get();
    }

    public void setZPosition(double zPosition) {
        this.zPosition.set(zPosition);
    }
}
