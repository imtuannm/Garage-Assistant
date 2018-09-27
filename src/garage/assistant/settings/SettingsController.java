package garage.assistant.settings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class SettingsController implements Initializable {
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //display the default values
        initDefaultValues();
    }    

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        String urn = username.getText().replaceAll("[^\\w\\s]","");
        String pass = password.getText();//hash & write to the preference file
        
        //set values
        Preferences preferences = Preferences.getPreferences();
        preferences.setUsername(urn);
        preferences.setPassword(pass);
        
        //write config to file
        //existing preference will return
        //if not -> create a new one
        Preferences.writePreferencesToFile(preferences);
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        ((Stage)username.getScene().getWindow()).close();
    }

    private void initDefaultValues() {
        //get the Preferences object from the gson file
        Preferences preferences = Preferences.getPreferences();
        
        //set into textfields
        username.setText(String.valueOf(preferences.getUsername()));
        password.setText(String.valueOf(preferences.getPassword()));
    }
    
}
