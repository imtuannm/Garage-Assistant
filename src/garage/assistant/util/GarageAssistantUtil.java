package garage.assistant.util;

import garage.assistant.settings.Preferences;
import garage.assistant.ui.listmotorbike.MotorbikeListController;
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
    
    //Category
    public static final String VEHICLE_1 = "Motorbike";
    public static final String VEHICLE_2 = "Car";
    public static final String VEHICLE_3 = "Container";
    
    //status
    public static final String STATUS_M1 = "Under Maintenance";
    public static final String STATUS_0 = "Issuing";
    public static final String STATUS_1 = "Available";
    public static final String STATUS_2 = "Booked";
    
    public static void setStageIcon(Stage stage) {
        stage.getIcons().add(new Image(IMG));
    }
    
    public static double getFineAmount(int totalDays, int expectedReturnDay , int baseFee, int finePercent) {
        double fine = 0;
        int fineDays = totalDays - expectedReturnDay;
        
        if (fineDays > 0) {
            fine = fineDays * baseFee * finePercent / 100;
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
        //john.smith@example.com(.us)
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
    
    public static String vehicleStatus(int value) {
        String stringStatus = null;
        switch (value) {
            case -1:
                stringStatus = STATUS_M1;
                break;
            case 0:
                stringStatus = STATUS_0;
                break;
            case 1:
                stringStatus = STATUS_1;
                break;
            case 2:
                stringStatus = STATUS_2;
                break;
            default:
                stringStatus = "404";
                break;
        }
        return stringStatus;
    }
}