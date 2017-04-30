package presenter;

import gui.BlastWindow.BlastController;
import javafx.concurrent.Task;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.BlastClient.BlastClient;
import model.Protein;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gvdambros on 1/28/17.
 */
public class BlastPresenter {

    String query;
    Task task = null;

    public BlastPresenter(String query) {
        this.query = query;
    }

    public void setBindings(BlastController blastController, Stage blastStage){

        ToggleGroup programToggleGroup = new ToggleGroup();
        blastController.blastnProgram.setToggleGroup(programToggleGroup);
        blastController.blastpProgram.setToggleGroup(programToggleGroup);
        blastController.rpsblastProgram.setToggleGroup(programToggleGroup);
        blastController.megablastProgram.setToggleGroup(programToggleGroup);
        blastController.blastxProgram.setToggleGroup(programToggleGroup);
        blastController.blastpProgram.setSelected(true);

        ToggleGroup databaseToggleGroup = new ToggleGroup();
        blastController.database1.setToggleGroup(databaseToggleGroup);
        blastController.database2.setToggleGroup(databaseToggleGroup);
        blastController.database3.setToggleGroup(databaseToggleGroup);
        blastController.database4.setToggleGroup(databaseToggleGroup);
        blastController.nrDatabase.setToggleGroup(databaseToggleGroup);
        blastController.nrDatabase.setSelected(true);

        blastController.runButton.setOnAction(event -> {
            String database = ((MenuItem) databaseToggleGroup.getSelectedToggle()).getText();
            String program =  ((MenuItem) programToggleGroup.getSelectedToggle()).getText();

            System.out.println(program + " " + database + ".");

            BlastClient blastClient = new BlastClient();

            task = new Task<Void>() {
                @Override public Void call() throws InterruptedException {

                    blastController.progressBar.setVisible(true);

                    blastClient.startSearch(query.substring(1,30));

                    System.err.println("Request id: " + blastClient.getRequestId());
                    System.err.println("Estimated time: " + blastClient.getEstimatedTime() + "s");

                    int time = 0, estimatedTime = blastClient.getEstimatedTime() + 5;
                    updateProgress(time, estimatedTime);

                    BlastClient.Status status = null;
                    do {
                        if (status != null && !isCancelled()){
                            Thread.sleep(1000);
                            if(time + 1 < estimatedTime)time += 1;
                            updateProgress(time, estimatedTime);
                        }
                        status = blastClient.getStatus();
                    }
                    while (status == BlastClient.Status.searching);

                    switch (status) {
                        case hitsFound:
                            List<String> textList = blastClient.getAlignments();
                            StringBuilder textString = new StringBuilder();
                            for (String line : textList)
                                textString.append(line + "\n");
                            blastController.textArea.setText(textString.toString());
                            break;
                        case noHitsFound:
                            blastController.textArea.setText("No Hits...");
                            break;
                        default:
                            blastController.textArea.setText("Status: " + status);
                    }
                    updateProgress(0, -1);
                    return null;
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    blastController.textArea.setText("Task canceled");
                    blastController.progressBar.setVisible(false);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    blastController.progressBar.setVisible(false);
                }
            };

            blastController.progressBar.progressProperty().bind(task.progressProperty());

            new Thread(task).start();

        });

        blastController.resetButton.setOnAction(event -> {
            blastController.textArea.setText("");
            if(task != null) task.cancel();
        });

        blastController.closeButton.setOnAction(event -> {
            if(task != null) task.cancel();
            blastStage.close();
        });

    }
}
