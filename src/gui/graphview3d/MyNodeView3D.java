package gui.graphview3d;

import javafx.beans.property.FloatProperty;
import model.graph.MyNode;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/**
 * Created by gvdambros on 12/10/16.
 */
public class MyNodeView3D extends Group {

    private MyNode myNode;
    private FloatProperty xPosition, yPosition, zPosition;
    public Sphere sphere;

    private Material naturalMaterial;
    public double naturalSize;

    public MyNodeView3D(MyNode myNode, Point3D position) {
        this.myNode = myNode;

        Color color = myNode.getAtomInfo().getColor();
        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor( color.darker() );
        phongMaterial.setSpecularColor( color.brighter() );
        naturalMaterial = phongMaterial;
        naturalSize = myNode.getAtomInfo().getSize();

        this.sphere = new Sphere( myNode.getAtomInfo().getSize() );

        sphere.setMaterial(phongMaterial);

        xPosition = new SimpleFloatProperty((float) position.getX());
        yPosition = new SimpleFloatProperty((float) position.getY());
        zPosition = new SimpleFloatProperty((float) position.getZ());

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

    public FloatProperty xPositionProperty() {
        return xPosition;
    }

    public FloatProperty yPositionProperty() {
        return yPosition;
    }

    public FloatProperty zPositionProperty() {
        return zPosition;
    }

    public Point3D getPosition(){
        return new Point3D(getXPosition(), getYPosition(), getZPosition() );
    }

    public float getXPosition() {
        return xPosition.get();
    }

    public void setXPosition(float xPosition) {
        this.xPosition.set(xPosition);
    }

    public float getYPosition() {
        return yPosition.get();
    }

    public void setYPosition(float yPosition) {
        this.yPosition.set(yPosition);
    }

    public float getZPosition() {
        return zPosition.get();
    }

    public void setZPosition(float zPosition) {
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
