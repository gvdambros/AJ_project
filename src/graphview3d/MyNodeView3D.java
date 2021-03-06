package graphview3d;

import graph.MyNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
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
    private Sphere sphere;
    private Material naturalMaterial;

    public MyNodeView3D(MyNode myNode, Point3D position) {
        this.myNode = myNode;

        Color color = myNode.getAtomInfo().getColor();
        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor( color.darker() );
        phongMaterial.setSpecularColor( color.brighter() );
        naturalMaterial = phongMaterial;

        this.sphere = new Sphere( myNode.getAtomInfo().getSize() );
        sphere.setMaterial(phongMaterial);

        xPosition = new SimpleDoubleProperty(position.getX());
        yPosition = new SimpleDoubleProperty(position.getY());
        zPosition = new SimpleDoubleProperty(position.getZ());

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

    public Point3D getPosition(){
        return new Point3D(getXPosition(), getYPosition(), getZPosition() );
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

    public void paint(Color color){
        naturalMaterial = sphere.getMaterial();
        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor( color.darker() );
        phongMaterial.setSpecularColor( color.brighter() );
        sphere.setMaterial(phongMaterial);
    }

    public void paint(){
        sphere.setMaterial(naturalMaterial);
    }

}
