package graphview2d;

import presenter2d.Presenter2D;
import graph.MyGraph;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * Created by Gustavo Dambros on 03/12/2016.
 */
public class SimpleGraphViewer extends BorderPane {

    public MyGraphView2D myGraphView2D;
    public Button clear, embedding;
    public Pane centerPane;

    public SimpleGraphViewer(MyGraph myGraph, Presenter2D presenter2D) {

        MyGraphView2D myGraphView2D = new MyGraphView2D(myGraph, presenter2D);

        this.clear = new Button("Clear");
        this.embedding = new Button("Smart Embedding");
        this.myGraphView2D = myGraphView2D;
        centerPane = new Pane(myGraphView2D);
        presenter2D.setCenterPaneBindings(centerPane);

        createLayout();
    }

    private void createLayout() {
        setTop( new ToolBar(clear, embedding));
        setCenter(centerPane);
    }


}
