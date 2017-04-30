package graphview3d;

import graph.MyGraph;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import presenter3d.Presenter3D;

/**
 * Created by gvdambros on 1/14/17.
 */
public class SimpleGraphViewer extends BorderPane{

    public MyGraphView3D myGraphView3D;
    public Button clear, loadFile;
    ToolBar toolBar;

    public Pane centerPane;
    public SubScene subScene;
    public PerspectiveCamera perspectiveCamera;

    public TextArea textArea;

    public MenuItem c1, c2, c3, c4;
    public MenuButton chartsMenuButton;
    public PieChart pieChart;

    public VBox rightPane;

    public SimpleGraphViewer(MyGraph myGraph, Presenter3D presenter3D, Stage stage) {

        this.setPrefWidth(600);

        this.clear = new Button("Clear");
        this.loadFile = new Button("Load File");

        toolBar = new ToolBar(clear, loadFile);


        this.myGraphView3D =  new MyGraphView3D(myGraph, presenter3D);

        this.centerPane = new Pane();
        this.subScene = new SubScene(myGraphView3D, 400, 400, true, SceneAntialiasing.BALANCED);

        this.textArea = new TextArea();

        rightPane = new VBox();

        chartsMenuButton = new MenuButton("Charts");

        this.c1 = new MenuItem("Amino Acids");
        this.c2 = new MenuItem("Helix Amino Acids");
        this.c3 = new MenuItem("Sheets Amino Acids");
        this.c4 = new MenuItem("Fourth Chart");

        this.pieChart = new PieChart();

        createLayout();

        presenter3D.setBindings(this, stage);
        presenter3D.setSceneBindings(centerPane, subScene, myGraphView3D);

    }

    private void createLayout() {

        this.setTop(toolBar);
        this.setTextField();
        this.setRightPane();
        this.set3DField();
    }

    private void setRightPane() {
        chartsMenuButton.getItems().addAll(c1,c2,c3,c4);
        rightPane.getChildren().addAll(chartsMenuButton, pieChart);
        rightPane.setPrefWidth(200);
        this.setRight(rightPane);
    }


    private void addAxes(){
        PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final Box xAxis = new Box(1000, 1, 1);
        xAxis.setMaterial(greenMaterial);
        final Box yAxis = new Box(1, 1000, 1);
        yAxis.setMaterial(blueMaterial);
        final Box zAxis = new Box(1, 1, 1000);
        zAxis.setMaterial(redMaterial);
        myGraphView3D.getChildren().addAll(xAxis, yAxis, zAxis);
    }


    private void setTextField() {
        textArea.setPromptText("Please, load a PDB file.");
        textArea.setPrefHeight(120);
        textArea.setFont( Font.font(java.awt.Font.MONOSPACED,28));
        textArea.setEditable(false);
        this.setBottom(textArea);
    }

    private void set3DField(){

        setCamera();

        centerPane.getChildren().add(subScene);
        subScene.widthProperty().bind( this.widthProperty().subtract( rightPane.widthProperty() ) );
        subScene.heightProperty().bind( this.heightProperty().subtract( textArea.heightProperty().add( toolBar.heightProperty()  ) ) );

        subScene.widthProperty().addListener(event ->{
            myGraphView3D.moveToCenter(new Point3D( subScene.getWidth()/2, subScene.getHeight()/2, 0));
        });

        subScene.heightProperty().addListener(event ->{
            myGraphView3D.moveToCenter(new Point3D( subScene.getWidth()/2, subScene.getHeight()/2, 0));
        });

        this.setCenter(centerPane);
    }

    private void setCamera(){
        perspectiveCamera = new PerspectiveCamera(true);
        perspectiveCamera.translateXProperty().bind(subScene.widthProperty().divide(2));
        perspectiveCamera.translateYProperty().bind(subScene.heightProperty().divide(2));
        perspectiveCamera.setTranslateZ(-100);
        perspectiveCamera.setNearClip(0.001);
        perspectiveCamera.setFarClip(Double.MAX_VALUE);
        subScene.setCamera(perspectiveCamera);
    }


    public void disableAll(boolean disable) {
            clear.setDisable(disable);  loadFile.setDisable(disable);
    }
}
