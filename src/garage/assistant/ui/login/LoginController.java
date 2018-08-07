package garage.assistant.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.settings.Preferences;
import garage.assistant.ui.main.MainController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    
    Preferences preference; //store the username & pass
    @FXML
    private Label titleLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
    }    

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        titleLabel.setText("Garage Assistand Log in");
        titleLabel.setStyle("-fx-background-color:BLACK;-fx-text-fill:WHITE");

        String usrName = username.getText();
        String pass = DigestUtils.shaHex(password.getText());//compare with the stored password in config file
    
        if(usrName.equals(preference.getUsername()) && pass.equals(preference.getPassword())) {
            closeStage();
            loadMain();
        } else {
            titleLabel.setText("Invalid input");
            titleLabel.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:WHITE");
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);//close the app
    }

    private void closeStage() {
        ((Stage)username.getScene().getWindow()).close();
    }
    
    //call MAIN window
    void loadMain() {
        try {
            Parent parent  = FXMLLoader.load(getClass().getResource("/garage/assistant/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Garage Assistant");
            stage.setScene(new Scene(parent));
            
            //set icon
            Image ico = new Image("/garage/assistant/resources/garage.png");
            stage.getIcons().add(ico);
            
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}