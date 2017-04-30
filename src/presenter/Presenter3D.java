package presenter;

import javafx.collections.ListChangeListener;
import model.PDBProcessor.PDBProcessor;
import gui.graphview3d.*;
import gui.BlastWindow.BlastController;
import gui.SimpleProteinViewer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.chart.PieChart;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Protein;
import model.graph.InvalidEdge;
import model.graph.MyEdge;
import model.sequence.AminoAcid;
import model.sequence.Helix;
import model.sequence.Sheet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gvdambros on 12/10/16.
 */
public class Presenter3D {

    private Protein protein;

    public double draggedPositionX;
    public double draggedPositionY;
    public ObjectProperty<MyExceptionView3D> exceptionProperty;
    public Property<Transform> graphTransforms;

    public HashMap<String, Point3D> atomsPosition;

    private IntegerProperty selectedProperty;

    public Presenter3D(Protein protein) {

        this.protein = protein;

        draggedPositionX = 0;
        draggedPositionY = 0;

        exceptionProperty = new SimpleObjectProperty<>();

        graphTransforms = new SimpleObjectProperty<>(new Rotate());

        atomsPosition = new HashMap<>();

        selectedProperty = new SimpleIntegerProperty(-1);

    }

    public void setNodeBindings(MyNodeView3D myNodeView3D) {

        myNodeView3D.setOnMouseClicked(event -> {
            selectedProperty.setValue( myNodeView3D.getMyNode().getResidualNumber() );
            event.consume();
        });

        myNodeView3D.setOnMouseEntered(event -> {
            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(200));
            scaleTransition.setNode(myNodeView3D);
            scaleTransition.setToX(3);
            scaleTransition.setToY(3);
            scaleTransition.setToZ(3);
            scaleTransition.play();
        });

        myNodeView3D.setOnMouseExited(event -> {
            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setDuration(Duration.millis(200));
            scaleTransition.setNode(myNodeView3D);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.setToZ(1);
            scaleTransition.play();
        });

    }

    public void setSceneBindings(Pane pane, SubScene subscene, MyGraphView3D myGraphView3D) {

        graphTransforms.addListener((observable, oldValue, newValue) -> {
            myGraphView3D.getTransforms().setAll(newValue);
        });

        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                graphTransforms.setValue(new Rotate());
            }
        });

        pane.setOnScroll(event -> {

            double s;
            if(event.getDeltaY() > 0) s = 1.2;
            else s = 1.0/1.2;
            Scale scale = new Scale(s, s, s);
            //scale.pivotXProperty().bind(subscene.widthProperty().divide(2));
            //scale.pivotYProperty().bind(subscene.heightProperty().divide(2));
            graphTransforms.setValue(scale.createConcatenation(graphTransforms.getValue()));
        });

        pane.setOnMouseReleased(event -> {
            draggedPositionX = 0;
            draggedPositionY = 0;
        });

        pane.setOnMousePressed(event -> {
            draggedPositionX = event.getSceneX();
            draggedPositionY = event.getSceneY();
        });

        pane.setOnMouseDragged(event -> {

            double deltaX = draggedPositionX - event.getSceneX();
            double deltaY = draggedPositionY - event.getSceneY();

            Transform transform;

            // Rotation
            Point3D perpendicularVector = new Point3D(-deltaY, deltaX, 0);
            double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            transform = new Rotate(length, perpendicularVector);
            //((Rotate)transform).pivotXProperty().bind(pane.widthProperty().divide(2));
            //((Rotate)transform).pivotYProperty().bind(pane.heightProperty().divide(2));


            graphTransforms.setValue(transform.createConcatenation(graphTransforms.getValue()));

            draggedPositionX = event.getSceneX();
            draggedPositionY = event.getSceneY();

            event.consume();
        });
    }

    public void setBindings(SimpleProteinViewer simpleProteinViewer, Stage primaryStage){

        selectedProperty.addListener((observable, oldValue, newValue) -> {
            String text = "";
            simpleProteinViewer.myGraphView3D.unselectNodes((int) oldValue);
            try{
                text = protein.getSequence().getPrimaryToString((int) newValue - 1) + "\n" + protein.getSequence().getSecondaryToString((int) newValue - 1);
            } catch (IndexOutOfBoundsException e){
                // TODO: 1/30/17
            }
            simpleProteinViewer.textArea.setText(text);
            simpleProteinViewer.myGraphView3D.selectNodes((int) newValue);

        });

        simpleProteinViewer.loadFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDB files (*.pdb)", "*.pdb");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File file = fileChooser.showOpenDialog(primaryStage);

            if(file != null){
                protein.clear();
                simpleProteinViewer.clear();
                PDBProcessor pdbProcessor = new PDBProcessor(file);
                pdbProcessor.run(protein, atomsPosition);
                simpleProteinViewer.disableSliders(false);
                simpleProteinViewer.disableButtons(false);
                this.selectedProperty.setValue(-1);
                simpleProteinViewer.myGraphView3D.moveToCenter( new Point3D( simpleProteinViewer.subScene.getWidth()/2, simpleProteinViewer.subScene.getHeight()/2, 0));
            }
        });


        simpleProteinViewer.visualization1.setOnAction(event -> {
            protein.getMyGraph().getObservableEdges().clear();
            protein.getMyGraph().getObservableHelixes().clear();
            protein.getMyGraph().getObservableRibbons().clear();
            simpleProteinViewer.disableSliders(false);
            simpleProteinViewer.edgeSlider.setValue(0.25f);
            simpleProteinViewer.nodeSlider.setValue(1f);
            protein.createGraph();
        });

        simpleProteinViewer.visualization2.setOnAction(event -> {
            protein.getMyGraph().getObservableEdges().clear();
            protein.getMyGraph().getObservableHelixes().clear();
            protein.getMyGraph().getObservableRibbons().clear();
            simpleProteinViewer.disableSliders(true);
            simpleProteinViewer.edgeSlider.setValue(0f);
            simpleProteinViewer.nodeSlider.setValue(0.5f);
            protein.createRibbon();
        });

        simpleProteinViewer.visualization3.setOnAction(event -> {
            try {
                protein.getMyGraph().getObservableEdges().clear();
                protein.getMyGraph().getObservableHelixes().clear();
                protein.getMyGraph().getObservableRibbons().clear();
                simpleProteinViewer.disableSliders(true);
                simpleProteinViewer.edgeSlider.setValue(0.25f);
                simpleProteinViewer.nodeSlider.setValue(0f);
                protein.createSimpleCartoon();
            } catch (InvalidEdge invalidEdge) {
                invalidEdge.printStackTrace();
            }
        });

        simpleProteinViewer.chart1.setOnAction(event -> {
            simpleProteinViewer.pieChart.setVisible(true);
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableList( new ArrayList<>() );
            HashMap<String, Integer> countAminoAcidInSequence = protein.getSequence().getCountAminoAcids();
            for( String key: countAminoAcidInSequence.keySet()){
                pieChartData.add( new PieChart.Data( key, countAminoAcidInSequence.get(key) ));
            }
            simpleProteinViewer.pieChart.setData(pieChartData);
        });

        simpleProteinViewer.chart2.setOnAction(event -> {
            simpleProteinViewer.pieChart.setVisible(true);
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableList( new ArrayList<>() );
            HashMap<String, Integer> countAminoAcidInHelixes = protein.getSequence().getCountAminoAcidsInHelixes();
            for( String key: countAminoAcidInHelixes.keySet()){
                pieChartData.add( new PieChart.Data( key, countAminoAcidInHelixes.get(key) ));
            }
            simpleProteinViewer.pieChart.setData(pieChartData);
        });

        simpleProteinViewer.chart3.setOnAction(event -> {
            simpleProteinViewer.pieChart.setVisible(true);
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableList( new ArrayList<>() );
            HashMap<String, Integer> countAminoAcidInSheets = protein.getSequence().getCountAminoAcidsInSheets();
            for( String key: countAminoAcidInSheets.keySet()){
                pieChartData.add( new PieChart.Data( key, countAminoAcidInSheets.get(key) ));
            }
            simpleProteinViewer.pieChart.setData(pieChartData);
        });

        simpleProteinViewer.clear.setOnAction(event -> {
            simpleProteinViewer.disableButtons(true);
            simpleProteinViewer.loadFile.setDisable(true);

            ScaleTransition scaleTransition = new ScaleTransition();
            scaleTransition.setToX(0);
            scaleTransition.setToY(0);
            scaleTransition.setToZ(0);
            scaleTransition.setDuration(Duration.seconds(1));

            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setByValue(0);
            fadeTransition.setDuration(Duration.seconds(1));

            ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
            parallelTransition.setNode( simpleProteinViewer.myGraphView3D );

            parallelTransition.setOnFinished(ended -> {
                simpleProteinViewer.myGraphView3D.opacityProperty().setValue(1);
                simpleProteinViewer.myGraphView3D.scaleXProperty().setValue(1);
                simpleProteinViewer.myGraphView3D.scaleYProperty().setValue(1);
                simpleProteinViewer.myGraphView3D.scaleZProperty().setValue(1);
                protein.clear();
                simpleProteinViewer.clear();
                simpleProteinViewer.loadFile.setDisable(false);
            });
            this.selectedProperty.setValue(-1);
            simpleProteinViewer.textArea.setText("");
            parallelTransition.play();
        });

        simpleProteinViewer.blastButton.setOnAction(event -> {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../gui/BlastWindow/BlastLayout.fxml"));
                Parent root = fxmlLoader.load();
                BlastController blastController = fxmlLoader.<BlastController>getController();
                BlastPresenter blastPresenter = new BlastPresenter(protein.getSequence().getPrimaryToString());
                Scene scene = new Scene(root);
                Stage blastStage = new Stage();
                blastPresenter.setBindings(blastController, blastStage);
                blastStage.initModality(Modality.APPLICATION_MODAL);
                blastStage.setScene(scene);
                blastStage.setTitle("Blast");
                blastStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        simpleProteinViewer.textArea.setOnMouseClicked(event -> {

            int splitPosition = simpleProteinViewer.textArea.getCaretPosition();
            int selected = selectedProperty.getValue();

            // clicked in the second part
            if(splitPosition >= protein.getSequence().length() + 1){
                splitPosition -= (protein.getSequence().length() + 1);
                if(selected >= 0) {
                    if (selected <= splitPosition) splitPosition -= 4;
                    else splitPosition -=2;
                }
            }
            //clicked first part
            else {
                if(selected >= 0 && selected <= splitPosition) splitPosition-=2;
            }
            this.selectedProperty.set( splitPosition + 1);
        });

        protein.getSequence().getAminoAcids().addListener( new ListChangeListener<AminoAcid>() {
            @Override
            public void onChanged(Change<? extends AminoAcid> c) {
                simpleProteinViewer.textArea.setText(protein.getSequence().toString());
            }
        });

        protein.getSequence().getHelixes().addListener( new ListChangeListener<Helix>() {
            @Override
            public void onChanged(Change<? extends Helix> c) {
                simpleProteinViewer.textArea.setText(protein.getSequence().toString());
            }
        });

        protein.getSequence().getSheets().addListener( new ListChangeListener<Sheet>() {
            @Override
            public void onChanged(Change<? extends Sheet> c) {
                simpleProteinViewer.textArea.setText(protein.getSequence().toString());
            }
        });

    }
}
