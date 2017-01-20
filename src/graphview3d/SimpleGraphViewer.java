package graphview3d;

import graph.MyGraph;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import presenter3d.Presenter3D;

/**
 * Created by gvdambros on 1/14/17.
 */
public class SimpleGraphViewer extends VBox{

    public MyGraphView3D myGraphView3D;
    public Button clear, embedding, loadFile;

    public StackPane centerPane;
    public SubScene subScene;
    public PerspectiveCamera perspectiveCamera;

    public TextArea textArea;

    public SimpleGraphViewer(MyGraph myGraph, Presenter3D presenter3D, Stage stage) {

        this.clear = new Button("Clear");
        this.embedding = new Button("Smart Embedding");
        this.loadFile = new Button("Load File");

        this.myGraphView3D =  new MyGraphView3D(myGraph, presenter3D);
        this.centerPane = new StackPane();
        this.subScene = new SubScene(centerPane, 400, 400, true, SceneAntialiasing.BALANCED);

        this.textArea = new TextArea();
        textArea.setFont( Font.font(java.awt.Font.MONOSPACED,12));

        createLayout();

        presenter3D.setBindings(this, stage);
        presenter3D.setSceneBindings(centerPane, myGraphView3D, perspectiveCamera);

    }

    private void createLayout() {
        this.getChildren().add(new ToolBar(clear, embedding, loadFile));
        this.set3DField();
        this.setTextField();
    }

    private void setTextField() {
        textArea.setPrefHeight(300);
        textArea.setPrefWidth(300);
        this.getChildren().add(textArea);
    }

    private void set3DField(){

        centerPane.setPrefHeight(400);
        centerPane.setPrefWidth(400);
        centerPane.getChildren().add(myGraphView3D);
        this.getChildren().add(centerPane);
        setCamera();

    }

    private void setCamera(){
        perspectiveCamera = new PerspectiveCamera(true);
        perspectiveCamera.translateXProperty().bind(subScene.widthProperty().divide(2));
        perspectiveCamera.translateYProperty().bind(subScene.heightProperty().divide(2));
        perspectiveCamera.setTranslateZ(-2500);
        perspectiveCamera.setNearClip(0.001);
        perspectiveCamera.setFarClip(Double.MAX_VALUE);
        subScene.setCamera(perspectiveCamera);
    }


    public void disableAll(boolean disable) {
            clear.setDisable(disable); embedding.setDisable(disable); loadFile.setDisable(disable);
    }
}
