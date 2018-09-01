package garage.assistant.util;

import garage.assistant.ui.main.MainController;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GarageAssistantUtil {
    private static final String IMG = "/garage/assistant/resources/icon.png";
    
    public static void setStageIcon(Stage stage) {
        stage.getIcons().add(new Image(IMG));
    }
    
    //call another window in-app
    public static void loadWindow(URL lct, String title, Stage parentStage) {
        try {
            Parent parent = FXMLLoader.load(lct);
            Stage stage = null;
            
            if(parentStage != null) {//use for further features
                stage = parentStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            setStageIcon(stage); //set universal icon
            
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}