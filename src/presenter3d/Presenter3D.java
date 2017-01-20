    package presenter3d;

    import graph.*;
    import graphview3d.*;
    import javafx.animation.FadeTransition;
    import javafx.animation.ParallelTransition;
    import javafx.animation.ScaleTransition;
    import javafx.beans.InvalidationListener;
    import javafx.beans.property.*;
    import javafx.geometry.Point3D;
    import javafx.scene.PerspectiveCamera;
    import javafx.scene.input.KeyCode;
    import javafx.scene.layout.StackPane;
    import javafx.scene.transform.Rotate;
    import javafx.scene.transform.Scale;
    import javafx.scene.transform.Transform;
    import javafx.scene.transform.Translate;
    import javafx.stage.FileChooser;
    import javafx.stage.Stage;
    import javafx.util.Duration;

    import java.io.*;
    import java.nio.file.Files;
    import java.util.HashMap;
    import java.util.List;

    /**
     * Created by gvdambros on 12/10/16.
     */
    public class Presenter3D {

        private MyGraph myGraph;
        private ObjectProperty<MyNode> nodeToBeDeleted;
        private ObjectProperty<MyEdge> edgeToBeDeleted;
        private MyNode nodeToBeConnected = null;
        public DoubleProperty clickedPositionX;
        public DoubleProperty clickedPositionY;
        public DoubleProperty clickedPositionZ;
        public double draggedPositionX;
        public double draggedPositionY;
        public ObjectProperty<MyExceptionView3D> exceptionProperty;
        public Property<Transform> graphTransforms;

        public ObjectProperty<Sequence> sequenceProperty;

        public HashMap<String, Point3D> atomsPosition;

        public Presenter3D(MyGraph myGraph) {

            this.myGraph = myGraph;

            nodeToBeDeleted = new SimpleObjectProperty<>();
            edgeToBeDeleted = new SimpleObjectProperty<>();

            clickedPositionX = new SimpleDoubleProperty();
            clickedPositionY = new SimpleDoubleProperty();
            clickedPositionZ = new SimpleDoubleProperty();

            draggedPositionX = 0;
            draggedPositionY = 0;

            exceptionProperty = new SimpleObjectProperty<>();

            graphTransforms = new SimpleObjectProperty<>(new Rotate());

            sequenceProperty = new SimpleObjectProperty<>(new Sequence());

            atomsPosition = new HashMap<>();

            setBindings();
        }

        private void setBindings() {
            nodeToBeDeleted.addListener((observable, oldValue, newValue) -> {
                myGraph.removeNode(newValue);
            });

            edgeToBeDeleted.addListener((observable, oldValue, newValue) -> {
                myGraph.removeEdge(newValue);
            });
        }


        public void setEdgeBindings(MyEdgeView3D myEdgeView3D) {
            myEdgeView3D.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    nodeToBeConnected = null;
                    edgeToBeDeleted.setValue(myEdgeView3D.getMyEdge());
                }
                event.consume();
            });

        }

        public void setNodeBindings(MyNodeView3D myNodeView3D) {

            myNodeView3D.setOnMouseDragged(event -> {

                System.out.println("Dragged position: " + draggedPositionX + " " + draggedPositionY);

                double deltaX = event.getSceneX() - draggedPositionX;
                double deltaY = event.getSceneY() - draggedPositionY;

                System.out.println("Delta: " + deltaX + " " + deltaY);

                myNodeView3D.setXPosition(myNodeView3D.getXPosition() + deltaX);
                myNodeView3D.setYPosition(myNodeView3D.getYPosition() + deltaY);

                draggedPositionX = event.getSceneX();
                draggedPositionY = event.getSceneY();

                nodeToBeConnected = null;
                event.consume();


                event.consume();
            });

            myNodeView3D.setOnMouseReleased(event -> {
                draggedPositionX = 0;
                draggedPositionY = 0;
            });

            myNodeView3D.setOnMousePressed(event -> {
                draggedPositionX = event.getSceneX();
                draggedPositionY = event.getSceneY();
            });

            myNodeView3D.setOnMouseClicked(event -> {
                if (event.isShiftDown()) {
                    if (nodeToBeConnected == null) {
                        nodeToBeConnected = myNodeView3D.getMyNode();
                    } else {
                        try {
                            myGraph.connectNodes(nodeToBeConnected, myNodeView3D.getMyNode());
                        } catch (InvalidEdge invalidEdge) {
                            invalidEdge.printStackTrace();
                        }
                        nodeToBeConnected = null;
                    }
                } else if (event.getClickCount() == 2) {
                    nodeToBeConnected = null;
                    nodeToBeDeleted.setValue(myNodeView3D.getMyNode());
                }
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

        public void setSceneBindings(StackPane scene, MyGraphView3D myGraphView3D, PerspectiveCamera perspectiveCamera) {

            graphTransforms.addListener((observable, oldValue, newValue) -> {
                myGraphView3D.getTransforms().setAll(newValue);
            });

            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.R) {
                    graphTransforms.setValue(new Rotate());
                }
            });

            scene.setOnScroll(event -> {

                double s = 1 + event.getDeltaY() * 0.005;
                Scale scale = new Scale(s, s, s);
                scale.pivotXProperty().bind(scene.widthProperty().divide(2));
                scale.pivotYProperty().bind(scene.heightProperty().divide(2));
                graphTransforms.setValue(scale.createConcatenation(graphTransforms.getValue()));

            });

            scene.setOnMouseReleased(event -> {
                draggedPositionX = 0;
                draggedPositionY = 0;
            });

            scene.setOnMousePressed(event -> {
                draggedPositionX = event.getSceneX();
                draggedPositionY = event.getSceneY();
            });

            scene.setOnMouseDragged(event -> {
                System.out.println("Dragged position: " + draggedPositionX + " " + draggedPositionY);

                double deltaX = draggedPositionX - event.getSceneX();
                double deltaY = draggedPositionY - event.getSceneY();

                System.out.println("Delta: " + deltaX + " " + deltaY);

                Transform transform;

                if (event.isShiftDown()) {
                    // Translate
                    transform = new Translate(deltaX, deltaY, 0);
                } else {
                    // Rotation
                    Point3D perpendicularVector = new Point3D(-deltaY, deltaX, 0);
                    double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                   Rotate r = new Rotate(length, perpendicularVector);
                    r.pivotXProperty().bind(scene.widthProperty().divide(2));
                    r.pivotYProperty().bind(scene.heightProperty().divide(2));
                    transform = r;

                }

                graphTransforms.setValue(transform.createConcatenation(graphTransforms.getValue()));

                draggedPositionX = event.getSceneX();
                draggedPositionY = event.getSceneY();

                nodeToBeConnected = null;
                event.consume();
            });
        }

        public void setBindings(SimpleGraphViewer simpleGraphViewer, Stage stage){

            simpleGraphViewer.loadFile.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDB files (*.pdb)", "*.pdb");
                fileChooser.getExtensionFilters().add(extensionFilter);
                File file = fileChooser.showOpenDialog(stage);

                if(file != null){
                    sequenceProperty.getValue().clear();
                    processPDBFile(file);
                }

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


                parallelTransition.play();
            });

            sequenceProperty.getValue().aminoAcids.addListener((InvalidationListener) observable -> {
                simpleGraphViewer.textArea.setText( sequenceProperty.getValue().toString() );
            });

            sequenceProperty.getValue().helixes.addListener((InvalidationListener) observable -> {
                simpleGraphViewer.textArea.setText( sequenceProperty.getValue().toString() );
            });

            sequenceProperty.getValue().sheets.addListener((InvalidationListener) observable -> {
                simpleGraphViewer.textArea.setText( sequenceProperty.getValue().toString() );
            });

        }

        private void processPDBFile(File file) {
            try {

                // getLayoutBound - text area size

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
                                sequenceProperty.getValue().addAminoAcid( new AminoAcid(temp.substring(i, i+3)) );
                                if(++count >= max) break;
                            }
                            break;
                        case "HELIX ":
                            id = temp.substring(MyConstants.HELIX_ID_POSITION, MyConstants.HELIX_ID_POSITION + 3);
                            begin = Integer.parseInt(temp.substring(MyConstants.HELIX_BEGIN_POSITION, MyConstants.HELIX_BEGIN_POSITION + 3).replace(" ",""));
                            end = Integer.parseInt(temp.substring(MyConstants.HELIX_END_POSITION, MyConstants.HELIX_END_POSITION + 3).replace(" ",""));
                            Helix helix = new Helix( id, begin, end );
                            sequenceProperty.getValue().addHelix( helix );
                            System.out.println(helix.toString());
                            break;
                        case "SHEET ":
                            id = temp.substring(MyConstants.SHEET_ID_POSITION, MyConstants.SHEET_ID_POSITION + 3);
                            if(!lastID.equals(id) || !lastOption.equals(option)){
                                sheet = new Sheet();
                                sequenceProperty.getValue().addSheet(sheet);
                            }
                            begin = Integer.parseInt(temp.substring(MyConstants.STRAND_BEGIN_POSITION, MyConstants.STRAND_BEGIN_POSITION + 3).replace(" ",""));
                            end = Integer.parseInt(temp.substring(MyConstants.STRAND_END_POSITION, MyConstants.STRAND_END_POSITION + 3).replace(" ",""));
                            Strand strand = new Strand ( id, begin, end );
                            sheet.addStrand( strand );
                            break;
                        case "ATOM  ":
                            id = temp.substring(MyConstants.ATOM_ID_POSITION, MyConstants.ATOM_ID_POSITION + 4);
                            String atomType = temp.substring(MyConstants.ATOM_TYPE_POSITION, MyConstants.ATOM_TYPE_POSITION + 3).replace(" ","");
                            double X = Double.parseDouble(temp.substring(MyConstants.ATOM_X_POSITION, MyConstants.ATOM_X_POSITION + 7).replace(" ",""));
                            double Y = Double.parseDouble(temp.substring(MyConstants.ATOM_Y_POSITION, MyConstants.ATOM_Y_POSITION + 7).replace(" ",""));
                            double Z = Double.parseDouble(temp.substring(MyConstants.ATOM_Z_POSITION, MyConstants.ATOM_Z_POSITION + 7).replace(" ",""));
                            MyNode myNode;
                            try{
                                myNode = new MyNode ( id, atomType );
                                atomsPosition.put(id, new Point3D(X, Y, Z));
                                auxConnection.put(myNode.getAtomInfo().getType(), myNode);
                                myGraph.addNode( myNode );
                                myGraph.connectNodes(  auxConnection.get(myNode.getAtomInfo().connectTo()), myNode );
                            }
                            catch(InvalidAtomType e){
                                // break;
                            }
                            catch (Exception e) {
                                // TODO: 1/20/17
                                // System.out.println(auxConnection.toString());
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
