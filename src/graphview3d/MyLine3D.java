package graphview3d;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

/**
 * Created by Neuroteam on 08.12.2016.
 */
public class MyLine3D extends Group{

    public MyLine3D(DoubleProperty startXProperty, DoubleProperty startYProperty, DoubleProperty startZProperty,
                    DoubleProperty endXProperty, DoubleProperty endYProperty, DoubleProperty endZProperty, Color color){

        System.out.println(startXProperty.getValue() + " " + startYProperty.getValue()+ " " +startZProperty.getValue() +
                " " +endXProperty.getValue()+ " " +endYProperty.getValue()+ " " +endZProperty.getValue()+ " ");

        Cylinder cylinder = new Cylinder();
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

        cylinder.setRadius(5);

        this.getChildren().add(cylinder);

    }

    public MyLine3D(double startXProperty, double startYProperty, double startZProperty,
                    double endXProperty, double endYProperty, double endZProperty, Color color){

        Cylinder cylinder = new Cylinder();
        Point3D direction = new Point3D(endXProperty - startXProperty, endYProperty - startYProperty, endZProperty - startZProperty);
        Point3D perpendicular = direction.crossProduct(0,1,0);

        double angle = direction.angle(0,1,0);

        System.out.println(angle);

        System.out.println(perpendicular);

        cylinder.getTransforms().add( new Rotate(angle, perpendicular));

        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor(Color.BLACK.darker());
        phongMaterial.setSpecularColor(Color.WHITE);

        cylinder.setMaterial(phongMaterial);

        cylinder.setTranslateX((endXProperty + startXProperty)/2);
        cylinder.setTranslateY((endYProperty + startYProperty)/2);
        cylinder.setTranslateZ((endZProperty + startZProperty)/2);
        cylinder.setHeight(endYProperty - startYProperty);
        cylinder.setRadius(5);

        System.out.println(cylinder);

        this.getChildren().add(cylinder);




    }


}
