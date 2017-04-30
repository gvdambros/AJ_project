package gui.graphview3d;

import javafx.scene.control.Alert;

/**
 * Created by gvdambros on 11/27/16.
 */
public class MyAlertView3D extends Alert {

    public MyAlertView3D(AlertType alertType, String message) {
        super(alertType);
        this.setTitle("Error Dialog");
        this.setHeaderText(null);
        this.setContentText(message);
    }
}
