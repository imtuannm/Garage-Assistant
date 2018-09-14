package garage.assistant.util;

import garage.assistant.settings.Preferences;
import garage.assistant.ui.main.MainController;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GarageAssistantUtil {
    private static final String IMG = "/garage/assistant/resources/icon.png";
    
    public static final String VEHICLE_1 = "Motorbike";
    public static final String VEHICLE_2 = "Car";
    public static final String VEHICLE_3 = "Self-Driving Car";
    public enum VEHICLE {
        VEHICLE_1, VEHICLE_2, VEHICLE_3;
    }
    
    public static void setStageIcon(Stage stage) {
        stage.getIcons().add(new Image(IMG));
    }
    
    public static Float getFineAmount(int totalDays) {
        Preferences pref = Preferences.getPreferences();
        Integer fineDays = totalDays - pref.getnDaysWithoutFine();
        Float fine = 0f;
        if (fineDays > 0) {
            fine = fineDays * pref.getFinePerDay();
        }
        return fine;
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
    
    public static boolean validateEmailAddress(String emailID) {
        //string@domain.xxx(.xx)
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(emailID).matches();
    }
    
    public static String categorizeVehicle(int value) {
        String vehicleCategory = null;
        switch (value) {
        case 1:
            vehicleCategory = VEHICLE_1;
            break;
        case 2:
            vehicleCategory = VEHICLE_2;
            break;
        case 3:
            vehicleCategory = VEHICLE_3;
            break;
        default:
            vehicleCategory = "404";
            break;
        }
        return vehicleCategory;
    }
}