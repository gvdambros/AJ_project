package gui.graphview3d;

import javafx.beans.property.FloatProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import model.graph.MyNode;
import model.graph.MyRibbon;
import sun.security.x509.X509CertInfo;

/**
 * Created by Neuroteam on 31.01.2017.
 */

public class MyRibbonView3D extends Group {

    private MeshView meshView;
    private MyRibbon myRibbon;

    public MyRibbonView3D(MyRibbon myRibbon, MyNodeView3D ca, MyNodeView3D cb, MyNodeView3D nca, MyNodeView3D ncb) {

        this.myRibbon = myRibbon;

        float[] points = {
                cb.getXPosition(),  cb.getYPosition(), cb.getZPosition(), // cß1
                2*ca.getXPosition() - cb.getXPosition(),   2*ca.getYPosition() - cb.getYPosition(),  2*ca.getZPosition() - cb.getZPosition(),  // cß'1
                ncb.getXPosition(),  ncb.getYPosition(), ncb.getZPosition(),  // cß2
                2*nca.getXPosition() - ncb.getXPosition(),   2*nca.getYPosition() - ncb.getYPosition(),  2*nca.getZPosition() - ncb.getZPosition() // cß'2
        };

        System.out.println(cb.getXPosition() + " " + cb.getYPosition() + " " + cb.getZPosition());

        int[] faces = {
                0,0, 1,0, 2,0,
                0,0, 2,0, 3,0,
                0,0, 2,0, 1,0,
                0,0, 3,0, 2,0
        };

        TriangleMesh triangleMesh = new TriangleMesh();
        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().addAll(0,0);
        triangleMesh.getFaces().setAll(faces);

        PhongMaterial phongMaterial = new PhongMaterial();
        phongMaterial.setDiffuseColor(Color.YELLOW.darker());
        phongMaterial.setSpecularColor(Color.YELLOW.brighter());

        meshView = new MeshView(triangleMesh);
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setMaterial(phongMaterial);


        this.getChildren().addAll(meshView);
    }

    public MyRibbon getMyRibbon() {
        return myRibbon;
    }
}
