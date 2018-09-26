package garage.assistant.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import garage.assistant.settings.Preferences;
import garage.assistant.ui.main.MainController;
import garage.assistant.util.GarageAssistantUtil;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.codec.digest.DigestUtils;
import javafx.scene.paint.Color;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private Text warnHolder;
    
    Preferences preference; //to get the stored the username & pass
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(()-> {
            preference = Preferences.getPreferences();
        }).start();
    }    

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String usrName = username.getText().replaceAll("[^\\w\\s]","");
        String pass = DigestUtils.shaHex(password.getText());//compare with the stored password in config file
    
        if(usrName.equals(preference.getUsername()) && pass.equals(preference.getPassword())) {//success
            loadMain();
            closeStage();
        } else {//warns user that entered wrong credentials
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
            
            warnHolder.setText("Wrong credential(s)!");//change the text from LOGIN
            warnHolder.setFill(Color.web("#E452E4"));
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
            Parent parent = FXMLLoader.load(getClass().getResource("/garage/assistant/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Garage Assistant");    
            stage.setScene(new Scene(parent));
            GarageAssistantUtil.setStageIcon(stage);
            
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}