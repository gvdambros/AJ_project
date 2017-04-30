package gui.graphview3d;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

/**
 * Created by Neuroteam on 08.12.2016.
 */
public class MyLine3D extends Group{

    private Cylinder cylinder;

    public MyLine3D(FloatProperty startXProperty, FloatProperty startYProperty, FloatProperty startZProperty,
                    FloatProperty endXProperty, FloatProperty endYProperty, FloatProperty endZProperty, double radius, Color color){

        cylinder = new Cylinder();
        Point3D point = new Point3D(endXProperty.getValue() - startXProperty.getValue(), endYProperty.getValue() - startYProperty.getValue(), endZProperty.getValue() - startZProperty.getValue());
        Point3D perpendicularAxis = point.crossProduct(0,-1,0);

        double angle = point.angle(0,1,0);

        cylinder.getTransforms().add( new Rotate(angle, perpendicularAxis));

        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor(color);
        phongMaterial.setSpecularColor(Color.WHITE);

        cylinder.setMaterial(phongMaterial);

        cylinder.setTranslateX((endXProperty.getValue() + startXProperty.getValue())/2);
        cylinder.setTranslateY((endYProperty.getValue() + startYProperty.getValue())/2);
        cylinder.setTranslateZ((endZProperty.getValue() + startZProperty.getValue())/2);

        double length = Math.sqrt( Math.pow( endXProperty.getValue() - startXProperty.getValue(), 2) +
                Math.pow( endYProperty.getValue() - startYProperty.getValue(), 2)  +
                Math.pow( endZProperty.getValue() - startZProperty.getValue(), 2) );

        cylinder.setHeight(length);
        cylinder.setRadius( radius );

        this.getChildren().add(cylinder);

    }

    public void setCylinderRadius(double radius){
        cylinder.setRadius(radius);
    }


}
