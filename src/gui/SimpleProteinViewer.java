package gui;

import model.Protein;
import gui.graphview3d.MyGraphView3D;
import javafx.geometry.Point3D;
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
import presenter.Presenter3D;

/**
 * Created by gvdambros on 1/14/17.
 */
public class SimpleProteinViewer extends BorderPane{

    public MyGraphView3D myGraphView3D;
    public Button clear, loadFile, blastButton;
    ToolBar toolBar;

    public Pane centerPane;
    public SubScene subScene;
    public PerspectiveCamera perspectiveCamera;

    public VBox bottomPane;
    public GridPane configurations;
    public Label nodeLabel;
    public Slider nodeSlider;
    public Label edgeLabel;
    public Slider edgeSlider;
    public TextArea textArea;

    public VBox rightPane;
    public MenuButton chartMenuButton, visualizationMenuButton;
    public MenuItem chart1, chart2, chart3, visualization1, visualization2, visualization3;
    public PieChart pieChart;


    public SimpleProteinViewer(Protein protein, Presenter3D presenter3D, Stage stage) {

        this.setPrefWidth(600);

        this.clear = new Button("Clear");
        clear.setDisable(true);
        this.loadFile = new Button("Load File");
        blastButton = new Button("Blast");
        blastButton.setDisable(true);

        toolBar = new ToolBar(clear, loadFile, blastButton);

        this.myGraphView3D =  new MyGraphView3D(protein.getMyGraph(), presenter3D);

        this.centerPane = new Pane();
        this.subScene = new SubScene(myGraphView3D, 400, 400, true, SceneAntialiasing.BALANCED);

        bottomPane = new VBox();
        configurations = new GridPane();
        nodeLabel = new Label("Node Size");
        nodeSlider = new Slider(0,2,1);
        edgeLabel = new Label("Edge Size");
        edgeSlider = new Slider(0,0.5,.25);
        this.textArea = new TextArea();

        rightPane = new VBox();
        chartMenuButton = new MenuButton("Charts");
        visualizationMenuButton = new MenuButton("Visualizaions");
        chartMenuButton.setDisable(true);
        this.chart1 = new MenuItem("Amino Acids");
        this.chart2 = new MenuItem("Helix Amino Acids");
        this.chart3 = new MenuItem("Sheets Amino Acids");
        this.visualization1 = new MenuItem("Graph View Based");
        this.visualization2 = new MenuItem("Ribbon");
        this.visualization3 = new MenuItem("Simple Cartoon");
        this.pieChart = new PieChart();

        createLayout();

        setBindings();

        presenter3D.setBindings(this, stage);
        presenter3D.setSceneBindings(centerPane, subScene, myGraphView3D);

    }

    private void setBindings() {
        myGraphView3D.baseNodeSize.bind( nodeSlider.valueProperty() );
        myGraphView3D.baseEdgeSize.bind( edgeSlider.valueProperty() );
    }

    private void createLayout() {
        this.setTop(toolBar);
        this.setBottomPane();
        this.setRightPane();
        this.set3DField();
    }

    private void setRightPane() {
        chartMenuButton.getItems().addAll(chart1, chart2, chart3);
        visualizationMenuButton.getItems().addAll(visualization1, visualization2, visualization3);
        rightPane.getChildren().addAll(chartMenuButton, pieChart, visualizationMenuButton);
        rightPane.setPrefWidth(200);
        this.setRight(rightPane);
    }

    private void setBottomPane(){
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(12);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(88);
        configurations.prefWidthProperty().bind(this.widthProperty().subtract(rightPane.widthProperty()));
        configurations.getColumnConstraints().addAll(col1, col2);
        configurations.add(nodeLabel,0,0);
        configurations.add(nodeSlider,1,0);
        configurations.add(edgeLabel,0,1);
        configurations.add(edgeSlider,1,1);
        nodeSlider.setDisable(true);
        edgeSlider.setDisable(true);
        bottomPane.getChildren().addAll(configurations, textArea);
        setTextArea();
        this.setBottom(bottomPane);
    }

    private void setTextArea() {
        textArea.setPromptText("Please, load a PDB file.");
        textArea.setPrefHeight(120);
        textArea.setFont( Font.font(java.awt.Font.MONOSPACED,28));
        textArea.setEditable(false);
    }

    private void set3DField(){

        setCamera();

        centerPane.getChildren().add(subScene);
        subScene.widthProperty().bind( this.widthProperty().subtract( rightPane.widthProperty() ) );
        subScene.heightProperty().bind( this.heightProperty().subtract( bottomPane.heightProperty().add( toolBar.heightProperty()  ) ) );

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

    public void disableButtons(boolean disable) {
        clear.setDisable(disable); visualizationMenuButton.setDisable(disable); chartMenuButton.setDisable(disable) ; blastButton.setDisable(disable);
    }

    public void clear(){
        pieChart.setVisible(false);
    }

    public void disableSliders(boolean disable) { nodeSlider.setDisable(disable); edgeSlider.setDisable(disable); }

    public void zeroSliderValues() {
        nodeSlider.setValue(0); edgeSlider.setValue(0);
    }
}
