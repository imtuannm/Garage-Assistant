package garage.assistant.ui.main;

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
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        Image image = new Image("/garage/assistant/viewable/garage.png");
        stage.getIcons().add(image);
        stage.setTitle("Garage Assistant");
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}