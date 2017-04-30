package sample;

import model.Protein;
import gui.SimpleProteinViewer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import presenter.Presenter3D;


public class Main extends Application {

    public void start(Stage primaryStage) throws Exception {

        Protein protein = new Protein();

        Presenter3D presenter3D = new Presenter3D(protein);

        SimpleProteinViewer simpleProteinViewer = new SimpleProteinViewer(protein, presenter3D, primaryStage);

        Scene scene = new Scene(simpleProteinViewer, 600, 800);

        primaryStage.setTitle("Simple Protein Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}