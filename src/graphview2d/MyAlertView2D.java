package graphview2d;

import javafx.scene.control.Alert;

/**
 * Created by gvdambros on 11/27/16.
 */
public class MyAlertView2D extends Alert {

    public MyAlertView2D(AlertType alertType, String message) {
        super(alertType);
        this.setTitle("Error Dialog");
        this.setHeaderText(null);
        this.setContentText(message);
    }
}
