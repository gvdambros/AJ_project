package sample;

import graph.MyGraph;
import graphview3d.SimpleGraphViewer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import presenter3d.Presenter3D;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        MyGraph myGraph = new MyGraph();

        Presenter3D presenter3D = new Presenter3D(myGraph);

        SimpleGraphViewer simpleGraphViewer = new SimpleGraphViewer(myGraph, presenter3D, primaryStage);

        Scene scene = new Scene(simpleGraphViewer, 800, 800);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}