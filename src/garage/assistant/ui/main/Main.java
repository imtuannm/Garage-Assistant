package garage.assistant.ui.main;

import garage.assistant.database.DatabaseHandler;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/garage/assistant/ui/login/login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        Image image = new Image("/garage/assistant/resources/garage.png");
        stage.getIcons().add(image);
        stage.setTitle("Garage Assistant");
        stage.show();
        
        //for better performance
        //create a multi-thread so as not to affect the UI
        new Thread(() -> { //lambda expression
            DatabaseHandler.getInstance(); // <- caused the delay while opening the app
        }).start();
        
//        new Thread(new Runnable() {
//            @Override
//                public void run() {
//                    DatabaseHandler.getInstance();
//            }
//        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}