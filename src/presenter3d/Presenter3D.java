    package presenter3d;

    import graph.*;
    import graphview3d.*;
    import javafx.animation.FadeTransition;
    import javafx.animation.ParallelTransition;
    import javafx.animation.ScaleTransition;
    import javafx.beans.InvalidationListener;
    import javafx.beans.property.*;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.geometry.Point3D;
    import javafx.scene.Node;
    import javafx.scene.PerspectiveCamera;
    import javafx.scene.SubScene;
    import javafx.scene.chart.PieChart;
    import javafx.scene.input.KeyCode;
    import javafx.scene.layout.Pane;
    import javafx.scene.transform.Rotate;
    import javafx.scene.transform.Scale;
    import javafx.scene.transform.Transform;
    import javafx.scene.transform.Translate;
    import javafx.stage.FileChooser;
    import javafx.stage.Stage;
    import javafx.util.Duration;

    import java.io.*;
    import java.nio.file.Files;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;

    /**
     * Created by gvdambros on 12/10/16.
     */
    public class Presenter3D {

        private MyGraph myGraph;

        public double draggedPositionX;
        public double draggedPositionY;
        public ObjectProperty<MyExceptionView3D> exceptionProperty;
        public Property<Transform> graphTransforms;

        public Sequence sequence;

        public HashMap<String, Point3D> atomsPosition;

        private IntegerProperty selectedProperty;

        public Presenter3D(MyGraph myGraph) {

            this.myGraph = myGraph;

            draggedPositionX = 0;
            draggedPositionY = 0;

            exceptionProperty = new SimpleObjectProperty<>();

            graphTransforms = new SimpleObjectProperty<>(new Rotate());

            sequence = new Sequence();

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
                if(event.getDeltaY() > 0) s = 2;
                else s = 1.0/2;
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

                if (event.isShiftDown()) {
                    // Translate
                    transform = new Translate(deltaX, deltaY, 0);
                } else {
                    // Rotation
                    Point3D perpendicularVector = new Point3D(-deltaY, deltaX, 0);
                    double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                    transform = new Rotate(length, perpendicularVector);
                    //((Rotate)transform).pivotXProperty().bind(pane.widthProperty().divide(2));
                    //((Rotate)transform).pivotYProperty().bind(pane.heightProperty().divide(2));
                }

                graphTransforms.setValue(transform.createConcatenation(graphTransforms.getValue()));

                draggedPositionX = event.getSceneX();
                draggedPositionY = event.getSceneY();

                event.consume();
            });
        }

        public void setBindings(SimpleGraphViewer simpleGraphViewer, Stage stage){

            selectedProperty.addListener((observable, oldValue, newValue) -> {
                simpleGraphViewer.myGraphView3D.unselectNodes((int) oldValue);
                String text = sequence.getPrimaryToString((int) newValue - 1)  + "\n" + sequence.getSecondaryToString((int) newValue - 1);
                simpleGraphViewer.textArea.setText( text );
                simpleGraphViewer.myGraphView3D.selectNodes((int) newValue);
            });

            simpleGraphViewer.loadFile.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDB files (*.pdb)", "*.pdb");
                fileChooser.getExtensionFilters().add(extensionFilter);
                File file = fileChooser.showOpenDialog(stage);

                if(file != null){
                    sequence.clear();
                    myGraph.clear();
                    processPDBFile(file);
                }

                this.selectedProperty.setValue(-1);

                simpleGraphViewer.myGraphView3D.moveToCenter( new Point3D( simpleGraphViewer.subScene.getWidth()/2, simpleGraphViewer.subScene.getHeight()/2, 0));
               
            });

            simpleGraphViewer.c1.setOnAction(event -> {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableList( new ArrayList<>() );
                HashMap<String, Integer> countAminoAcidInSequence = sequence.getCountAminoAcids();
                for( String key: countAminoAcidInSequence.keySet()){
                    pieChartData.add( new PieChart.Data( key, countAminoAcidInSequence.get(key) ));
                }
                simpleGraphViewer.pieChart.setData(pieChartData);
            });

            simpleGraphViewer.c2.setOnAction(event -> {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableList( new ArrayList<>() );
                HashMap<String, Integer> countAminoAcidInHelixes = sequence.getCountAminoAcidsInHelixes();
                for( String key: countAminoAcidInHelixes.keySet()){
                    pieChartData.add( new PieChart.Data( key, countAminoAcidInHelixes.get(key) ));
                }
                simpleGraphViewer.pieChart.setData(pieChartData);
            });

            simpleGraphViewer.c3.setOnAction(event -> {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableList( new ArrayList<>() );
                HashMap<String, Integer> countAminoAcidInSheets = sequence.getCountAminoAcidsInSheets();
                for( String key: countAminoAcidInSheets.keySet()){
                    pieChartData.add( new PieChart.Data( key, countAminoAcidInSheets.get(key) ));
                }
                simpleGraphViewer.pieChart.setData(pieChartData);
            });

            simpleGraphViewer.c4.setOnAction(event -> {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableList( new ArrayList<>() );
                HashMap<String, Integer> countAminoAcidInSequence = sequence.getCountAminoAcids();
                for( String key: countAminoAcidInSequence.keySet()){
                    pieChartData.add( new PieChart.Data( key, countAminoAcidInSequence.get(key) ));
                }
                simpleGraphViewer.pieChart.setData(pieChartData);
            });

            simpleGraphViewer.clear.setOnAction(event -> {
                simpleGraphViewer.disableAll(true);

                ScaleTransition scaleTransition = new ScaleTransition();
                scaleTransition.setToX(0);
                scaleTransition.setToY(0);
                scaleTransition.setToZ(0);
                scaleTransition.setDuration(Duration.seconds(1));

                FadeTransition fadeTransition = new FadeTransition();
                fadeTransition.setByValue(0);
                fadeTransition.setDuration(Duration.seconds(1));

                ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
                parallelTransition.setNode( simpleGraphViewer.myGraphView3D );

                parallelTransition.setOnFinished(ended -> {
                    simpleGraphViewer.myGraphView3D.opacityProperty().setValue(1);
                    simpleGraphViewer.myGraphView3D.scaleXProperty().setValue(1);
                    simpleGraphViewer.myGraphView3D.scaleYProperty().setValue(1);
                    simpleGraphViewer.myGraphView3D.scaleZProperty().setValue(1);
                    myGraph.clear();
                    simpleGraphViewer.disableAll(false);
                });
                this.selectedProperty.setValue(-1);
                simpleGraphViewer.textArea.setText("");
                parallelTransition.play();
            });

            simpleGraphViewer.textArea.setOnMouseClicked(event -> {

                int splitPosition = simpleGraphViewer.textArea.getCaretPosition();
                int selected = selectedProperty.getValue();

                // clicked in the third part
                if(splitPosition >= 2*sequence.getPrimaryToString().length() + 3){
                    splitPosition -= (2*sequence.getPrimaryToString().length() + 3);
                    if(selected >= 0) {
                        if (selected <= splitPosition) splitPosition -= 6;
                        else splitPosition -=4;
                    }
                }
                // clicked in the second part
                else {
                    if(splitPosition >= sequence.getPrimaryToString().length() + 2){
                        splitPosition -= (sequence.getPrimaryToString().length() + 2);
                        if(selected >= 0) {
                            if (selected <= splitPosition) splitPosition -= 4;
                            else splitPosition -=2;
                        }
                    }
                    //clicked first part
                    else {
                        if(selected >= 0 && selected <= splitPosition) splitPosition-=2;
                    }
                }

                this.selectedProperty.set( splitPosition + 1);
            });

            sequence.aminoAcids.addListener((InvalidationListener) observable -> {
                simpleGraphViewer.textArea.setText( sequence.toString() );
            });

            sequence.helixes.addListener((InvalidationListener) observable -> {
                simpleGraphViewer.textArea.setText( sequence.toString() );
            });

            sequence.sheets.addListener((InvalidationListener) observable -> {
                simpleGraphViewer.textArea.setText( sequence.toString() );
            });

        }

        private void processPDBFile(File file) {
            try {
                List<String> data = Files.readAllLines(file.toPath());

                String lastOption = "";
                String lastID = "";

                Sheet sheet = new Sheet();
                int count = 0;

                HashMap<AtomInfo.AtomsTypes, MyNode> auxConnection = new HashMap<>();

                for(String temp : data){

                    String option = temp.substring(MyConstants.OPT_BEGIN_POSITION, MyConstants.OPT_END_POSITION);
                    String id = ".";
                    int begin, end;
                    int max;

                    switch(option){
                        case "SEQRES":
                            max = Integer.parseInt(temp.substring(MyConstants.HELIX_LENGTH_POSITION, MyConstants.HELIX_LENGTH_POSITION + 3).replace(" ",""));
                            id = temp.substring(MyConstants.SEQRED_ID_POSITION, MyConstants.SEQRED_ID_POSITION + 3);
                            for(int i = MyConstants.SEQRES_BEGIN_POSITION; i < MyConstants.SEQRES_END_POSITION; i = i + MyConstants.SEQRES_OFFSET) {
                                sequence.addAminoAcid( new AminoAcid(temp.substring(i, i+3)) );
                                if(++count >= max) break;
                            }
                            break;
                        case "HELIX ":
                            id = temp.substring(MyConstants.HELIX_ID_POSITION, MyConstants.HELIX_ID_POSITION + 3);
                            begin = Integer.parseInt(temp.substring(MyConstants.HELIX_BEGIN_POSITION, MyConstants.HELIX_BEGIN_POSITION + 3).replace(" ",""));
                            end = Integer.parseInt(temp.substring(MyConstants.HELIX_END_POSITION, MyConstants.HELIX_END_POSITION + 3).replace(" ",""));
                            Helix helix = new Helix( id, begin - 1, end );
                            sequence.addHelix( helix );
                            break;
                        case "SHEET ":
                            id = temp.substring(MyConstants.SHEET_ID_POSITION, MyConstants.SHEET_ID_POSITION + 3);
                            if(!lastID.equals(id) || !lastOption.equals(option)){
                                sheet = new Sheet();
                                sequence.addSheet(sheet);
                            }
                            begin = Integer.parseInt(temp.substring(MyConstants.STRAND_BEGIN_POSITION, MyConstants.STRAND_BEGIN_POSITION + 3).replace(" ",""));
                            end = Integer.parseInt(temp.substring(MyConstants.STRAND_END_POSITION, MyConstants.STRAND_END_POSITION + 3).replace(" ",""));
                            Strand strand = new Strand ( id, begin - 1, end );
                            sheet.addStrand( strand );
                            break;
                        case "ATOM  ":
                            id = temp.substring(MyConstants.ATOM_ID_POSITION, MyConstants.ATOM_ID_POSITION + 4);
                            String atomType = temp.substring(MyConstants.ATOM_TYPE_POSITION, MyConstants.ATOM_TYPE_POSITION + 3).replace(" ","");
                            int residualNumber = Integer.parseInt(temp.substring(MyConstants.ATOM_RESIDUAL_POSITION, MyConstants.ATOM_RESIDUAL_POSITION + 3).replace(" ",""));
                            double X = Double.parseDouble(temp.substring(MyConstants.ATOM_X_POSITION, MyConstants.ATOM_X_POSITION + 7).replace(" ",""));
                            double Y = Double.parseDouble(temp.substring(MyConstants.ATOM_Y_POSITION, MyConstants.ATOM_Y_POSITION + 7).replace(" ",""));
                            double Z = Double.parseDouble(temp.substring(MyConstants.ATOM_Z_POSITION, MyConstants.ATOM_Z_POSITION + 7).replace(" ",""));
                            MyNode myNode;
                            try{
                                myNode = new MyNode ( id, atomType, residualNumber );
                                atomsPosition.put(id, new Point3D(X, Y, Z));
                                auxConnection.put(myNode.getAtomInfo().getType(), myNode);
                                myGraph.addNode( myNode );
                                // First line of ATOM has no one to connect to.
                                if(lastOption.equals(option)){
                                    myGraph.connectNodes( auxConnection.get(myNode.getAtomInfo().connectTo()), myNode );
                                }
                            } catch (Exception e) {
                                // TODO: 1/20/17
                                // e.printStackTrace();
                            }
                            break;
                    }

                    lastOption = option;
                    lastID = id;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
