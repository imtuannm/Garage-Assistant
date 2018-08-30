package garage.assistant.ui.main;

import garage.assistant.database.DatabaseHandler;
import garage.assistant.util.GarageAssistantUtil;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/garage/assistant/ui/login/login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        GarageAssistantUtil.setStageIcon(stage);
        stage.setTitle("Garage Assistant");
        stage.show();
        
        //for better performance
        //create a multi-thread so as not to affect the UI
        new Thread(() -> { //lambda expression
            DatabaseHandler.getInstance(); // <- caused the delay while opening the app
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}