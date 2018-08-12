package garage.assistant.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GarageAssistantUtil {
    private static final String IMG = "/garage/assistant/resources/icon.png";
    
    public static void setStageIcon(Stage stage) {
        stage.getIcons().add(new Image(IMG));
    }
}